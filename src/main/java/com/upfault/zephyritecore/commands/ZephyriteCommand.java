package com.upfault.zephyritecore.commands;

import com.upfault.zephyritecore.ZephyriteCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ZephyriteCommand implements CommandExecutor, TabCompleter {

	private final ZephyriteCore plugin;

	public ZephyriteCommand(ZephyriteCore plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Invalid command. Use /zephyrite about or /zephyrite reload.");
			return true;
		}

		if (args[0].equalsIgnoreCase("about")) {
			sender.sendMessage(ChatColor.AQUA + "ZephyriteCore " + ChatColor.GOLD + "Plugin Information:");
			sender.sendMessage(ChatColor.BLUE + "Version: " + ChatColor.YELLOW + plugin.getDescription().getVersion());
			sender.sendMessage(ChatColor.BLUE + "Developer: " + ChatColor.YELLOW + plugin.getDescription().getAuthors());
			sender.sendMessage(ChatColor.BLUE + "Description: " + ChatColor.YELLOW + plugin.getDescription().getDescription());
			sender.sendMessage(ChatColor.BLUE + "Website: " + ChatColor.YELLOW + plugin.getDescription().getWebsite());
			sender.sendMessage(ChatColor.BLUE + "Support: " + ChatColor.YELLOW + plugin.getDescription().getWebsite() + "/help");
			return true;
		}

		else if (args[0].equalsIgnoreCase("reload")) {
			plugin.reloadConfig();
			sender.sendMessage(ChatColor.GREEN + "ZephyriteCore configuration reloaded successfully.");
			return true;
		}

		sender.sendMessage(ChatColor.RED + "Invalid command. Use /zephyrite about or /zephyrite reload.");
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		if (args.length == 1) {
			List<String> completions = new ArrayList<>();
			List<String> options = Arrays.asList("about", "reload");
			for (String option : options) {
				if (option.startsWith(args[0].toLowerCase())) {
					completions.add(option);
				}
			}
			return completions;
		}
		return null;
	}
}
