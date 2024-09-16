package com.upfault.zephyritecore.utils;

import com.upfault.zephyritecore.ZephyriteCore;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseManager {

	private final ZephyriteCore plugin = ZephyriteCore.getPlugin();
	private Connection connection;

	public DatabaseManager() {
		try {
			setupDatabase();
			createTables();
		} catch (SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Could not set up MySQL database", e);
		}
	}

	private void setupDatabase() throws SQLException {
		String host = plugin.getConfig().getString("mysql.host");
		String database = plugin.getConfig().getString("mysql.database");
		String username = plugin.getConfig().getString("mysql.username");
		String password = plugin.getConfig().getString("mysql.password");
		int port = plugin.getConfig().getInt("mysql.port");

		String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false";

		connection = DriverManager.getConnection(url, username, password);
		plugin.getLogger().log(Level.INFO, "MySQL database connected successfully.");
	}

	private void createTables() throws SQLException {
		createTable("player_info", """
				CREATE TABLE IF NOT EXISTS player_info (
				    id INT AUTO_INCREMENT PRIMARY KEY,
				    uuid VARCHAR(36) NOT NULL UNIQUE,
				    name VARCHAR(100) NOT NULL,
				    player_rank VARCHAR(50) DEFAULT 'None',
				    join_date BIGINT DEFAULT 0,
				    last_login BIGINT DEFAULT 0,
				    last_logout BIGINT DEFAULT 0,
				    last_server VARCHAR(100) DEFAULT 'lobby-1'
				);
				""");


		createTable("player_stats", """
				CREATE TABLE IF NOT EXISTS player_stats (
				    id INT AUTO_INCREMENT PRIMARY KEY,
				    uuid VARCHAR(36) NOT NULL,
				    level INT DEFAULT 1,
				    coins INT DEFAULT 0,
				    FOREIGN KEY (uuid) REFERENCES player_info(uuid) ON DELETE CASCADE
				);
				""");

		createTable("player_command_states", """
				CREATE TABLE IF NOT EXISTS player_command_states (
				    id INT AUTO_INCREMENT PRIMARY KEY,
				    uuid VARCHAR(36) NOT NULL,
				    command_name VARCHAR(100) NOT NULL,
				    command_status BOOLEAN DEFAULT FALSE,
				    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
				    FOREIGN KEY (uuid) REFERENCES player_info(uuid) ON DELETE CASCADE,
				    UNIQUE (uuid, command_name)
				);
				""");

		createTable("player_friends", """
				    CREATE TABLE IF NOT EXISTS player_friends (
				        player_uuid VARCHAR(36) NOT NULL,
				        friend_uuid VARCHAR(36) NOT NULL,
				        is_online BOOLEAN DEFAULT FALSE,
				        FOREIGN KEY (player_uuid) REFERENCES player_info(uuid) ON DELETE CASCADE,
				        FOREIGN KEY (friend_uuid) REFERENCES player_info(uuid) ON DELETE CASCADE,
				        PRIMARY KEY (player_uuid, friend_uuid)
				    );
				""");
	}

	private void createTable(String tableName, String sql) throws SQLException {
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.executeUpdate();
			plugin.getLogger().log(Level.INFO, tableName + " table created or already exists.");
		} catch (SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to create table: " + tableName, e);
			throw e;
		}
	}

	public void addPlayer(UUID uuid, String name) {
		if (!playerExists(uuid)) {
			addPlayerInfo(uuid, name);
			addPlayerStats(uuid);
		} else {
			plugin.getLogger().log(Level.INFO, "Player " + name + " already exists in the database.");
		}
	}

	public void addPlayerInfo(UUID uuid, String name) {
		String sql = "INSERT INTO player_info (uuid, name, player_rank, join_date) VALUES (?, ?, 'None', ?)";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, uuid.toString());
			stmt.setString(2, name);
			stmt.setLong(3, System.currentTimeMillis());
			stmt.executeUpdate();
			plugin.getLogger().log(Level.INFO, "Player " + name + " added to player_info.");
		} catch (SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to add player to player_info.", e);
		}
	}

	public void addPlayerStats(UUID uuid) {
		String sql = "INSERT INTO player_stats (uuid, level, coins) VALUES (?, 1, 0)";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, uuid.toString());
			stmt.executeUpdate();
			plugin.getLogger().log(Level.INFO, "Player stats initialized for " + uuid);
		} catch (SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to add player stats.", e);
		}
	}

	public void addPlayerFriend(UUID playerUUID, UUID friendUUID) {
		String sql = "INSERT INTO player_friends (player_uuid, friend_uuid) VALUES (?, ?)";

		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, playerUUID.toString());
			stmt.setString(2, friendUUID.toString());
			stmt.executeUpdate();
			plugin.getLogger().log(Level.INFO, "Friend added: " + friendUUID + " for player: " + playerUUID);
		} catch (SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to add friend.", e);
		}
	}

	public int getOnlineFriends(UUID playerUuid) {
		String sql = "SELECT COUNT(*) FROM player_friends WHERE player_uuid = ? AND is_online = TRUE";

		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, playerUuid.toString());
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1);
				}
			}
		} catch (SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to retrieve online friends.", e);
		}
		return 0;
	}

	public void setFriendOnlineStatus(UUID playerUuid, UUID friendUuid, boolean isOnline) {
		String sql = "UPDATE player_friends SET is_online = ? WHERE player_uuid = ? AND friend_uuid = ?";

		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setBoolean(1, isOnline);
			stmt.setString(2, playerUuid.toString());
			stmt.setString(3, friendUuid.toString());
			stmt.executeUpdate();
		} catch (SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to update friend online status.", e);
		}
	}

	public void removePlayerFriend(UUID playerUUID, UUID friendUUID) {
		String sql = "DELETE FROM player_friends WHERE player_uuid = ? AND friend_uuid = ?";

		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, playerUUID.toString());
			stmt.setString(2, friendUUID.toString());
			stmt.executeUpdate();
			plugin.getLogger().log(Level.INFO, "Friend removed: " + friendUUID + " for player: " + playerUUID);
		} catch (SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to remove friend.", e);
		}
	}

	public List<UUID> getPlayerFriends(UUID playerUUID) {
		List<UUID> friends = new ArrayList<>();
		String sql = "SELECT friend_uuid FROM player_friends WHERE player_uuid = ?";

		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, playerUUID.toString());
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					friends.add(UUID.fromString(rs.getString("friend_uuid")));
				}
			}
		} catch (SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to get friends list.", e);
		}

		return friends;
	}


	public boolean playerExists(UUID uuid) {
		String sql = "SELECT uuid FROM player_info WHERE uuid = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, uuid.toString());
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to check if player exists.", e);
		}
		return false;
	}

	public <T> T getPlayerField(UUID uuid, String fieldName, Class<T> type, T defaultValue) {
		String sql = "SELECT " + fieldName + " FROM player_info WHERE uuid = ?";
		return getField(uuid, fieldName, type, defaultValue, sql);
	}

	public <T> T getPlayerFieldFromStats(UUID uuid, String fieldName, Class<T> type, T defaultValue) {
		String sql = "SELECT " + fieldName + " FROM player_stats WHERE uuid = ?";
		return getField(uuid, fieldName, type, defaultValue, sql);
	}

	public <T> T getField(UUID uuid, String fieldName, Class<T> type, T defaultValue, String sql) {
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, uuid.toString());
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					if (type == Integer.class) {
						return type.cast(rs.getInt(fieldName));
					} else if (type == String.class) {
						return type.cast(rs.getString(fieldName));
					}
				}
			}
		} catch (SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to retrieve " + fieldName, e);
		}
		return defaultValue;
	}

	public void updatePlayerField(UUID uuid, String fieldName, Object value) {
		String sql = "UPDATE player_info SET " + fieldName + " = ? WHERE uuid = ?";
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
		} catch (SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to update " + fieldName + " in player_info", e);
		}
	}


	public void updatePlayerStatsField(UUID uuid, String fieldName, int value) {
		updateField(uuid, fieldName, value, "player_stats");
	}

	public void updateField(UUID uuid, String fieldName, Object value, String tableName) {
		String sql = "UPDATE " + tableName + " SET " + fieldName + " = ? WHERE uuid = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			if (value instanceof String) {
				stmt.setString(1, (String) value);
			} else if (value instanceof Integer) {
				stmt.setInt(1, (Integer) value);
			}
			stmt.setString(2, uuid.toString());
			stmt.executeUpdate();
		} catch (SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to update " + fieldName + " in " + tableName, e);
		}
	}

	public boolean getPlayerCommandState(UUID uuid, String commandName) {
		String sql = "SELECT command_status FROM player_command_states WHERE uuid = ? AND command_name = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, uuid.toString());
			stmt.setString(2, commandName);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getBoolean("command_status");
				}
			}
		} catch (SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to get command state.", e);
		}
		return false;
	}

	public void setPlayerCommandState(UUID uuid, String commandName, boolean state) {
		String sql = "INSERT INTO player_command_states (uuid, command_name, command_status) " +
				"VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE command_status = VALUES(command_status)";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, uuid.toString());
			stmt.setString(2, commandName);
			stmt.setBoolean(3, state);
			stmt.executeUpdate();
		} catch (SQLException e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to update command state.", e);
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
