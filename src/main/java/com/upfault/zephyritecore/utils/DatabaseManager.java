package com.upfault.zephyritecore.utils;

import com.upfault.zephyritecore.ZephyriteCore;

import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

@SuppressWarnings("all")
public class DatabaseManager {

	private Connection connection;
	private final ZephyriteCore plugin = ZephyriteCore.getPlugin();

	public DatabaseManager() {
		try {
			setupDatabase();
			createPlayersTable();
		} catch (SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Could not set up MySQL database", e);
		}
	}

	private void setupDatabase() throws SQLException {
		String host = ZephyriteCore.getPlugin().getConfig().getString("mysql.host");
		String database = ZephyriteCore.getPlugin().getConfig().getString("mysql.database");
		String username = ZephyriteCore.getPlugin().getConfig().getString("mysql.username");
		String password = ZephyriteCore.getPlugin().getConfig().getString("mysql.password");
		int port = ZephyriteCore.getPlugin().getConfig().getInt("mysql.port");

		String url = "jdbc:mysql://" + host + ":" + port + "/" + database;

		connection = DriverManager.getConnection(url, username, password);
		plugin.getLogger().log(Level.INFO, "MySQL database has been connected.");
	}

	private void createPlayersTable() throws SQLException {
		String sql = "CREATE TABLE IF NOT EXISTS players (" +
				"id INT AUTO_INCREMENT PRIMARY KEY," +
				"uuid VARCHAR(36) NOT NULL," +
				"name VARCHAR(100) NOT NULL," +
				"player_rank VARCHAR(50) DEFAULT 'None'," +
				"coins INT DEFAULT 0," +
				"level INT DEFAULT 1," +
				"join_date BIGINT DEFAULT 0," +
				"last_login BIGINT DEFAULT 0," +
				"last_logout BIGINT DEFAULT 0," +
				"last_server VARCHAR(100) DEFAULT 'lobby-1'" +
				");";

		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.executeUpdate();
			plugin.getLogger().info("Players table created or already exists.");
		}
	}



	public void addPlayer(UUID uuid, String name) {
		if (!playerExists(uuid)) {
			String sql = "INSERT INTO players (uuid, name, player_rank, coins, level, join_date) " +
					"VALUES (?, ?, 'None', 0, 1, ?)";

			try (PreparedStatement stmt = connection.prepareStatement(sql)) {
				stmt.setString(1, uuid.toString());
				stmt.setString(2, name);
				stmt.setLong(3, System.currentTimeMillis());
				stmt.executeUpdate();
				plugin.getLogger().log(Level.INFO, "Player " + name + " added to the database.");
			} catch (SQLException e) {
				plugin.getLogger().log(Level.SEVERE, "Failed to add player to the database.", e);
			}
		} else {
			plugin.getLogger().log(Level.INFO, "Player " + name + " already exists in the database.");
		}
	}

	private boolean playerExists(UUID uuid) {
		String sql = "SELECT uuid FROM players WHERE uuid = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, uuid.toString());
			ResultSet rs = stmt.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to check if player exists in the database.", e);
		}
		return false;
	}


	public String getLastServer(UUID uuid) {
		return getPlayerField(uuid, "last_server", String.class, "lobby-1");
	}

	public void setLastServer(UUID uuid, String lastServer) {
		updatePlayerField(uuid, "last_server", lastServer);
	}

	public String getPlayerRank(UUID uuid) {
		return getPlayerField(uuid, "player_rank", String.class, "None");
	}
	public int getPlayerCoins(UUID uuid) {
		return getPlayerField(uuid, "coins", Integer.class, 0);
	}

	public int getPlayerLevel(UUID uuid) {
		return getPlayerField(uuid, "level", Integer.class, 1);
	}

	private <T> T getPlayerField(UUID uuid, String fieldName, Class<T> type, T defaultValue) {
//		plugin.getLogger().info("SQL Query: SELECT " + fieldName + " FROM players WHERE uuid = '" + uuid + "'");
		String sql = "SELECT " + fieldName + " FROM players WHERE uuid = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, uuid.toString());
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				if (type == Integer.class) {
					return type.cast(rs.getInt(fieldName));
				} else if (type == String.class) {
					return type.cast(rs.getString(fieldName));
				} else if (type == Long.class) {
					return type.cast(rs.getLong(fieldName));
				}
			}
		} catch (SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to retrieve player " + fieldName + ".", e);
		}
		return defaultValue;
	}

	public void setPlayerRank(UUID uuid, String rank) {
		updatePlayerField(uuid, "player_rank", rank);
	}

	public void setPlayerCoins(UUID uuid, int coins) {
		updatePlayerField(uuid, "coins", coins);
	}

	public void setPlayerLevel(UUID uuid, int level) {
		updatePlayerField(uuid, "level", level);
	}

	public void setLastLogin(UUID uuid, long timestamp) {
		updatePlayerField(uuid, "last_login", timestamp);
	}

	public void setLastLogout(UUID uuid, long timestamp) {
		updatePlayerField(uuid, "last_logout", timestamp);
	}

	private void updatePlayerField(UUID uuid, String fieldName, Object value) {
		String sql = "UPDATE players SET " + fieldName + " = ? WHERE uuid = ?";

		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			if (value instanceof String) {
				stmt.setString(1, (String) value);
			} else if (value instanceof Integer) {
				stmt.setInt(1, (Integer) value);
			} else if (value instanceof Long) {
				stmt.setLong(1, (Long) value);
			}
			stmt.setString(2, uuid.toString());
			stmt.executeUpdate();
//			plugin.getLogger().log(Level.INFO, "Player " + fieldName + " updated to " + value);
		} catch (SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to update player " + fieldName + ".", e);
		}
	}

	public void closeConnection() {
		if (connection != null) {
			try {
				connection.close();
				plugin.getLogger().log(Level.INFO, "MySQL connection closed.");
			} catch (SQLException e) {
				plugin.getLogger().log(Level.SEVERE, "Could not close MySQL connection", e);
			}
		}
	}
}
