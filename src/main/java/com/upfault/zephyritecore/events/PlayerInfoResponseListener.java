package com.upfault.zephyritecore.events;

import com.upfault.zephyritecore.ZephyriteCore;
import com.upfault.zephyritecore.utils.DatabaseManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class PlayerInfoResponseListener implements PluginMessageListener {

	private final DatabaseManager databaseManager;

	public PlayerInfoResponseListener(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}

	@Override
	public void onPluginMessageReceived(String channel, @NotNull Player player, byte[] message) {
		// Logging the channel received
		ZephyriteCore.getPlugin().getLogger().info("Received plugin message on channel: " + channel);

		if (!channel.equals("velocity:player_info")) {
			return;
		}

		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();
		// Logging the subchannel
		ZephyriteCore.getPlugin().getLogger().info("Subchannel: " + subchannel);

		if (subchannel.equals("ServerInfo")) {
			String serverName = in.readUTF();
			// Logging the server name received
			ZephyriteCore.getPlugin().getLogger().info("ServerInfo received, server name: " + serverName);

			player.sendMessage("You are connected to the server: " + serverName);

			UUID playerUUID = player.getUniqueId();
			ZephyriteCore.getPlugin().getLogger().info("Player UUID: " + playerUUID);

			if (databaseManager.playerExists(playerUUID)) {
				// Logging before updating the database
				ZephyriteCore.getPlugin().getLogger().info("Updating last_server for player " + playerUUID + " to " + serverName);

				databaseManager.updatePlayerField(playerUUID, "last_server", serverName);

				// Logging after attempting to update
				ZephyriteCore.getPlugin().getLogger().info("Updated last_server for player " + playerUUID);
			} else {
				ZephyriteCore.getPlugin().getLogger().warning("Player not found in database: " + playerUUID);
			}

		} else if (subchannel.equals("PlayerCount")) {
			try (DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(message))) {
				int totalPlayers = dataIn.readInt();
				ZephyriteCore.getPlugin().getLogger().info("Total players across proxy: " + totalPlayers);
			} catch (IOException e) {
				ZephyriteCore.getPlugin().getLogger().severe("Failed to read player count from plugin message: " + e.getMessage());
			}
		}
	}
}
