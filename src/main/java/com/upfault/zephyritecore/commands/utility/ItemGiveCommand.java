package com.upfault.zephyritecore.commands.utility;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemGiveCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can use this command!");
			return true;
		}

		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Usage: /i <item> <amount>");
			return true;
		}

		Player player = (Player) sender;
		Material material = Material.getMaterial(args[0].toUpperCase());
		int amount;

		try {
			amount = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			player.sendMessage(ChatColor.RED + "Invalid amount.");
			return true;
		}

		if (material == null) {
			player.sendMessage(ChatColor.RED + "Invalid item.");
			return true;
		}

		player.getInventory().addItem(new ItemStack(material, amount));
		player.sendMessage(ChatColor.GREEN + "You have been given " + ChatColor.YELLOW + amount + "x " + material.name() + ChatColor.GREEN +  ".");
		return true;
	}
}
