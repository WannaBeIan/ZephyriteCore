package com.upfault.zephyritecore.events;

import com.upfault.zephyritecore.ZephyriteCore;
import com.upfault.zephyritecore.api.scoreboard.LobbyScoreboard;
import com.upfault.zephyritecore.utils.DatabaseManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class PlayerJoinListener implements Listener {

	private final DatabaseManager databaseManager = ZephyriteCore.getPlugin().getDatabaseManager();
	private static final Random random = new Random();
	private static final List<String> joinMessages = Arrays.asList(
			" joined the lobby!",
			" has entered the lobby!",
			" is now in the house!",
			" has arrived!",
			" just landed in the lobby!",
			" is here!"
	);

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		event.setJoinMessage(generateJoinMessage(player));
		applyJoinProcedures(player);
	}

	private void applyJoinProcedures(Player player) {
		UUID playerUUID = player.getUniqueId();
		String playerName = player.getName();

		if (!databaseManager.playerExists(playerUUID)) {
			databaseManager.addPlayer(playerUUID, playerName);
		} else {
			databaseManager.updatePlayerField(playerUUID, "last_login", System.currentTimeMillis());
		}

		LobbyScoreboard.applyScoreboard(player);
	}


	private String generateJoinMessage(Player player) {
		String rank = databaseManager.getPlayerField(player.getUniqueId(), "player_rank", String.class, "None");
		String playerName = player.getName();
		String randomMessage = joinMessages.get(random.nextInt(joinMessages.size()));

		String arrows = ChatColor.AQUA + ">>> ";
		String arrowsEnd = ChatColor.AQUA + " <<<";

		return arrows +
				ChatColor.YELLOW + rank + " " +
				ChatColor.BLUE + playerName +
				ChatColor.YELLOW + randomMessage +
				arrowsEnd;
	}
}
