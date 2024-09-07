package com.upfault.zephyritecore.commands.utility;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class RenameCommand implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can use this command!");
			return true;
		}

		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Usage: /rename <new name>");
			return true;
		}

		Player player = (Player) sender;
		ItemStack itemInHand = player.getInventory().getItemInMainHand();

		if (!itemInHand.hasItemMeta()) {
			player.sendMessage(ChatColor.RED + "You must hold an item to rename.");
			return true;
		}

		ItemMeta meta = itemInHand.getItemMeta();
		assert meta != null;
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', String.join(" ", args)));
		itemInHand.setItemMeta(meta);

		player.sendMessage(ChatColor.GREEN + "Item renamed to " + ChatColor.YELLOW + String.join(" ", args) + ChatColor.GREEN + ".");
		return true;
	}
}
