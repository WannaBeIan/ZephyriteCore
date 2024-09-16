package com.upfault.zephyritecore.events;

import com.upfault.zephyritecore.ZephyriteCore;
import com.upfault.zephyritecore.api.scoreboard.LobbyScoreboard;
import com.upfault.zephyritecore.utils.DatabaseManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuitListener implements Listener {

	private final DatabaseManager databaseManager = ZephyriteCore.getPlugin().getDatabaseManager();

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		UUID playerUUID = player.getUniqueId();

		if (databaseManager.playerExists(playerUUID)) {
			databaseManager.updatePlayerField(playerUUID, "last_logout", System.currentTimeMillis());
		} else {
			ZephyriteCore.getPlugin().getLogger().warning("Player not found in database on quit: " + playerUUID);
		}

		LobbyScoreboard.removeScoreboard(player);
	}

}
