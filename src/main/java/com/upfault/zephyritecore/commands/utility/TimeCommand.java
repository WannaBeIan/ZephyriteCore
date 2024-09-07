package com.upfault.zephyritecore.commands.utility;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TimeCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		World world = Bukkit.getWorlds().get(0);
		String commandLabel = label.toLowerCase();

		switch (commandLabel) {
			case "day":
				world.setTime(1000);
				sender.sendMessage(ChatColor.GREEN + "Time set to day.");
				break;
			case "night":
				world.setTime(13000);
				sender.sendMessage(ChatColor.GREEN + "Time set to night.");
				break;
			case "morning":
				world.setTime(0);
				sender.sendMessage(ChatColor.GREEN + "Time set to morning.");
				break;
			case "dawn":
				world.setTime(23000);
				sender.sendMessage(ChatColor.GREEN + "Time set to dawn.");
				break;
			default:
				if (args.length < 1) {
					sender.sendMessage(ChatColor.RED + "Usage: /time <day/night/morning/dawn>");
					return true;
				}

				String timeArg = args[0].toLowerCase();
				switch (timeArg) {
					case "day":
						world.setTime(1000);
						sender.sendMessage(ChatColor.GREEN + "Time set to day.");
						break;
					case "night":
						world.setTime(13000);
						sender.sendMessage(ChatColor.GREEN + "Time set to night.");
						break;
					case "morning":
						world.setTime(0);
						sender.sendMessage(ChatColor.GREEN + "Time set to morning.");
						break;
					case "dawn":
						world.setTime(23000);
						sender.sendMessage(ChatColor.GREEN + "Time set to dawn.");
						break;
					default:
						sender.sendMessage(ChatColor.RED + "Invalid time. Use day, night, morning, or dawn.");
				}
		}
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		if (args.length == 1) {
			return Stream.of("day", "night", "morning", "dawn").filter(time -> time.startsWith(args[0].toLowerCase()))
					.collect(Collectors.toList());
		}
		return null;
	}
}
