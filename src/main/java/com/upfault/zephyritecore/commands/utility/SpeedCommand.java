package com.upfault.zephyritecore.commands.utility;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SpeedCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can use this command!");
			return true;
		}

		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Usage: /speed <1-10>");
			return true;
		}

		Player player = (Player) sender;
		int speedInput;

		try {
			speedInput = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			player.sendMessage(ChatColor.RED + "Invalid speed.");
			return true;
		}

		if (speedInput < 1 || speedInput > 10) {
			player.sendMessage(ChatColor.RED + "Speed must be between 1 and 10.");
			return true;
		}

		float speed = (speedInput - 1) * 0.1f;

		player.setWalkSpeed(speed);
		player.sendMessage(ChatColor.GREEN + "Walk speed set to " + ChatColor.YELLOW + speedInput);
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		if (args.length == 1) {
			return IntStream.rangeClosed(1, 10)
					.mapToObj(String::valueOf)
					.filter(speed -> speed.startsWith(args[0]))
					.collect(Collectors.toList());
		}
		return null;
	}
}
