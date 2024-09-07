package com.upfault.zephyritecore.commands.utility;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SpawnCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can use this command!");
			return true;
		}

		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Usage: /spawn <entity> [amount]");
			return true;
		}

		Player player = (Player) sender;
		EntityType entityType;

		try {
			entityType = EntityType.valueOf(args[0].toUpperCase());
		} catch (IllegalArgumentException e) {
			sender.sendMessage(ChatColor.RED + "Invalid entity.");
			return true;
		}

		int amount = 1;
		if (args.length > 1) {
			try {
				amount = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + "Invalid amount. Spawning 1 by default.");
			}
		}

		for (int i = 0; i < amount; i++) {
			player.getWorld().spawnEntity(player.getLocation(), entityType);
		}

		sender.sendMessage(ChatColor.GREEN + "Spawned " + ChatColor.YELLOW + amount + " " + entityType.name() + "(s).");
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		if (args.length == 1) {
			return Arrays.stream(EntityType.values())
					.map(EntityType::name)
					.filter(entity -> entity.toLowerCase().startsWith(args[0].toLowerCase()))
					.collect(Collectors.toList());
		}
		return null;
	}
}
