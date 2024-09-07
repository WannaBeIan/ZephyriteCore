package com.upfault.zephyritecore.commands.utility;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class WhoisCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Usage: /whois <player>");
			return true;
		}

		OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

		sender.sendMessage(ChatColor.GOLD + "Player Info:");
		sender.sendMessage(ChatColor.GOLD + "Name: " + ChatColor.YELLOW + target.getName());
		sender.sendMessage(ChatColor.GOLD + "UUID: " + ChatColor.YELLOW + target.getUniqueId());
		sender.sendMessage(ChatColor.GOLD + "Is Online: " + ChatColor.YELLOW + target.isOnline());
		sender.sendMessage(ChatColor.GOLD+ "Last Seen: " + ChatColor.YELLOW + target.getLastPlayed());
		sender.sendMessage(ChatColor.GOLD + "First Seen: " + ChatColor.YELLOW + target.getFirstPlayed());

		return true;
	}
}
