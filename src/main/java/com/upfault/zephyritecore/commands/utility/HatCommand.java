package com.upfault.zephyritecore.commands.utility;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HatCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can use this command!");
			return true;
		}

		Player player = (Player) sender;
		ItemStack itemInHand = player.getInventory().getItemInMainHand();

		if (itemInHand.getType().isAir()) {
			player.sendMessage(ChatColor.RED + "You must hold an item to use it as a hat!");
			return true;
		}

		ItemStack helmet = player.getInventory().getHelmet();
		player.getInventory().setHelmet(itemInHand);
		player.getInventory().setItemInMainHand(helmet);

		player.sendMessage(ChatColor.GREEN + "You are now wearing " + ChatColor.GOLD + itemInHand.getType().toString().toLowerCase() + ChatColor.GREEN + " as a hat.");
		return true;
	}
}
