package com.upfault.zephyritecore.events;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.upfault.zephyritecore.utils.DatabaseManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

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

		if (subchannel.equals("ServerInfo")) {
			String serverName = in.readUTF();
			player.sendMessage("You are connected to the server: " + serverName);

			UUID playerUUID = player.getUniqueId();
			databaseManager.setLastServer(playerUUID, serverName);
		}
	}
}
