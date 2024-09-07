package com.upfault.zephyritecore.events;

import com.upfault.zephyritecore.api.scoreboard.LobbyScoreboard;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		LobbyScoreboard.removeScoreboard(event.getPlayer());
	}
}
