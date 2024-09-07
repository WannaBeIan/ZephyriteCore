package com.upfault.zephyritecore.commands.utility;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TpCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can use this command!");
			return true;
		}

		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Usage: /tp <player>");
			return true;
		}

		Player target = Bukkit.getPlayerExact(args[0]);
		Player player = (Player) sender;

		if (target == null) {
			player.sendMessage(ChatColor.RED + "Player not found.");
			return true;
		}

		player.teleport(target.getLocation());
		player.sendMessage(ChatColor.GREEN + "Teleported to " + ChatColor.YELLOW + target.getName() + ".");
		return true;
	}
}
