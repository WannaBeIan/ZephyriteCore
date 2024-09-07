package com.upfault.zephyritecore.commands.utility;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GameModeCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can use this command!");
			return true;
		}

		Player player = (Player) sender;
		GameMode targetGameMode = null;

		switch (label.toLowerCase()) {
			case "gmc":
			case "creative":
				targetGameMode = GameMode.CREATIVE;
				break;
			case "gms":
			case "survival":
				targetGameMode = GameMode.SURVIVAL;
				break;
			case "gma":
			case "adventure":
				targetGameMode = GameMode.ADVENTURE;
				break;
			case "gmsp":
			case "spectator":
				targetGameMode = GameMode.SPECTATOR;
				break;
			case "gamemode":
				if (args.length > 0) {
					targetGameMode = getGameModeFromArgs(args[0]);
				}
				break;
		}

		if (targetGameMode == null) {
			player.sendMessage(ChatColor.RED + "Invalid game mode! Usage: /gamemode <mode>");
			return true;
		}

		player.setGameMode(targetGameMode);
		player.sendMessage(ChatColor.GREEN + "You are now in " + ChatColor.YELLOW + targetGameMode.toString().toLowerCase() + " mode.");
		return true;
	}

	private GameMode getGameModeFromArgs(String arg) {
		switch (arg.toLowerCase()) {
			case "creative":
			case "1":
				return GameMode.CREATIVE;
			case "survival":
			case "0":
				return GameMode.SURVIVAL;
			case "adventure":
			case "2":
				return GameMode.ADVENTURE;
			case "spectator":
			case "3":
				return GameMode.SPECTATOR;
			default:
				return null;
		}
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		if (args.length == 1) {
			List<String> gameModes = Arrays.asList("creative", "survival", "adventure", "spectator", "0", "1", "2", "3");
			return gameModes.stream().filter(mode -> mode.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
		}
		return null;
	}
}
