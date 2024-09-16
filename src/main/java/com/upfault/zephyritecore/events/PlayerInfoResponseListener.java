package com.upfault.zephyritecore.events;

import com.upfault.zephyritecore.ZephyriteCore;
import com.upfault.zephyritecore.utils.DatabaseManager;
import com.upfault.zephyritecore.utils.ScoreboardUtils;
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
		if (!channel.equals("velocity:player_info")) return;

		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();

		switch (subchannel) {
			case "ServerInfo":
				handleServerInfo(player, in);
				break;
			case "PlayerCount":
				handlePlayerCount(in);
				break;
			default:
				ZephyriteCore.getPlugin().getLogger().warning("Unknown subchannel: " + subchannel);
				break;
		}
	}

	private void handleServerInfo(Player player, ByteArrayDataInput in) {
		String serverName = in.readUTF();
		UUID playerUUID = player.getUniqueId();

		if (databaseManager.playerExists(playerUUID)) {
			databaseManager.updatePlayerField(playerUUID, "last_server", serverName);
		} else {
			ZephyriteCore.getPlugin().getLogger().warning("Player not found in database: " + playerUUID);
		}
	}

	private void handlePlayerCount(ByteArrayDataInput in) {
		try (DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(in.readUTF().getBytes()))) {
			int totalPlayers = dataIn.readInt();
			ScoreboardUtils.updateTotalPlayers(totalPlayers);
		} catch (IOException e) {
			ZephyriteCore.getPlugin().getLogger().severe("Failed to read player count: " + e.getMessage());
		}
	}
}
