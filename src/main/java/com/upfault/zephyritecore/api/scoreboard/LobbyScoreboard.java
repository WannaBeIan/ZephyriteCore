package com.upfault.zephyritecore.api.scoreboard;

import com.upfault.zephyritecore.ZephyriteCore;
import com.upfault.zephyritecore.utils.DatabaseManager;
import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.*;

public class LobbyScoreboard {

	public static final Map<UUID, FastBoard> boards = new HashMap<>();
	private static final String WEBSITE = ChatColor.DARK_PURPLE + "www.zephyritemc.com";
	private static final DatabaseManager databaseManager = ZephyriteCore.getPlugin().getDatabaseManager();

	public static void applyScoreboard(Player player) {
		FastBoard board = new FastBoard(player);
		boards.put(player.getUniqueId(), board);

		board.updateTitle(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "ZephyriteMC");

		List<String> lines = new ArrayList<>();
		String date = new SimpleDateFormat("MM/dd/yy").format(new Date());
		lines.add(ChatColor.GRAY + date + ChatColor.DARK_GRAY + " L25D");
		lines.add(ChatColor.DARK_GRAY + " ");
		lines.add(ChatColor.GOLD + "Rank: " + ChatColor.WHITE + getPlayerRank(player));
		lines.add(ChatColor.GOLD + "Level: " + ChatColor.WHITE + getPlayerLevel(player));
		lines.add(ChatColor.GOLD + "Coins: " + ChatColor.WHITE + getPlayerCoins(player));
		lines.add(ChatColor.DARK_GRAY + "  ");
		lines.add(ChatColor.GOLD + "Lobby: " + ChatColor.WHITE + getLobbyPlayerCount());
		lines.add(ChatColor.GOLD + "Players: " + ChatColor.WHITE + getTotalPlayers());
		lines.add(ChatColor.DARK_GRAY + "   ");
		lines.add(ChatColor.GOLD + "Friends Online: " + ChatColor.WHITE + getFriendsOnline());
		lines.add(ChatColor.DARK_GRAY + "    ");
		lines.add(WEBSITE);

		board.updateLines(lines);
	}

	public static void updateScoreboard(FastBoard board) {
		Player player = board.getPlayer();

		board.updateTitle(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "ZephyriteMC");

		List<String> lines = new ArrayList<>();
		String date = new SimpleDateFormat("MM/dd/yy").format(new Date());
		lines.add(ChatColor.GRAY + date + ChatColor.DARK_GRAY + " L25D");
		lines.add(ChatColor.DARK_GRAY + " ");
		lines.add(ChatColor.GOLD + "Rank: " + ChatColor.WHITE + getPlayerRank(player));
		lines.add(ChatColor.GOLD + "Level: " + ChatColor.WHITE + getPlayerLevel(player));
		lines.add(ChatColor.GOLD + "Coins: " + ChatColor.WHITE + getPlayerCoins(player));
		lines.add(ChatColor.DARK_GRAY + "  ");
		lines.add(ChatColor.GOLD + "Lobby: " + ChatColor.WHITE + getLobbyPlayerCount());
		lines.add(ChatColor.GOLD + "Players: " + ChatColor.WHITE + getTotalPlayers());
		lines.add(ChatColor.DARK_GRAY + "   ");
		lines.add(ChatColor.GOLD + "Friends Online: " + ChatColor.WHITE + getFriendsOnline());
		lines.add(ChatColor.DARK_GRAY + "    ");
		lines.add(WEBSITE);

		board.updateLines(lines);
	}

	private static String getPlayerRank(Player player) {
		return databaseManager.getPlayerRank(player.getUniqueId());
	}

	private static int getPlayerLevel(Player player) {
		return databaseManager.getPlayerLevel(player.getUniqueId());
	}

	private static int getPlayerCoins(Player player) {
		return databaseManager.getPlayerCoins(player.getUniqueId());
	}

	private static int getLobbyPlayerCount() {
		return Bukkit.getServer().getOnlinePlayers().size();
	}

	private static int getTotalPlayers() {
		return Bukkit.getServer().getOnlinePlayers().size();
	}

	private static int getFriendsOnline() {
		return 5;
	}

	public static void removeScoreboard(Player player) {
		boards.remove(player.getUniqueId());
	}
}
