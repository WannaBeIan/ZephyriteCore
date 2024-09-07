package com.upfault.zephyritecore.commands.utility;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.World;
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
import java.util.stream.Stream;

public class WeatherCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		Player player = (Player) sender;
		World world = player.getWorld();
		String commandLabel = label.toLowerCase();

		switch (commandLabel) {
			case "rain":
				world.setStorm(true);
				world.setThundering(false);
				sender.sendMessage(ChatColor.GREEN + "Weather changed to rain.");
				break;
			case "clear":
				world.setStorm(false);
				world.setThundering(false);
				sender.sendMessage(ChatColor.GREEN + "Weather changed to clear.");
				break;
			case "storm":
				world.setStorm(true);
				world.setThundering(true);
				sender.sendMessage(ChatColor.GREEN + "Weather changed to storm.");
				break;
			default:
				if (args.length < 1) {
					sender.sendMessage(ChatColor.RED + "Usage: /weather <rain/clear/storm>");
					return true;
				}

				String weatherArg = args[0].toLowerCase();
				switch (weatherArg) {
					case "rain":
						world.setStorm(true);
						world.setThundering(false);
						sender.sendMessage(ChatColor.GREEN + "Weather changed to rain.");
						break;
					case "clear":
						world.setStorm(false);
						world.setThundering(false);
						sender.sendMessage(ChatColor.GREEN + "Weather changed to clear.");
						break;
					case "storm":
						world.setStorm(true);
						world.setThundering(true);
						sender.sendMessage(ChatColor.GREEN + "Weather changed to storm.");
						break;
					default:
						sender.sendMessage(ChatColor.RED + "Invalid weather type.");
				}
		}
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		if (args.length == 1) {
			return Stream.of("rain", "clear", "storm").filter(weather -> weather.startsWith(args[0].toLowerCase()))
					.collect(Collectors.toList());
		}
		return null;
	}
}
