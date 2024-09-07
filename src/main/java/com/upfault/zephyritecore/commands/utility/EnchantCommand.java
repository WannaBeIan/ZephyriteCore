package com.upfault.zephyritecore.commands.utility;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EnchantCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can use this command!");
			return true;
		}

		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Usage: /enchant <enchantment> <level>");
			return true;
		}

		Player player = (Player) sender;
		ItemStack itemInHand = player.getInventory().getItemInMainHand();
		Enchantment enchantment = Enchantment.getByKey(Objects.requireNonNull(Enchantment.getByName(args[0].toUpperCase())).getKey());

		if (enchantment == null) {
			player.sendMessage(ChatColor.RED + "Invalid enchantment.");
			return true;
		}

		int requestedLevel;
		try {
			requestedLevel = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			player.sendMessage(ChatColor.RED + "Invalid level.");
			return true;
		}

		int maxLevel = enchantment.getMaxLevel();
		if (requestedLevel > maxLevel) {
			player.sendMessage(ChatColor.RED + "The maximum level for " + enchantment.getKey().getKey().replace('_', ' ') + " is " + maxLevel + ".");
			return true;
		}

		itemInHand.addUnsafeEnchantment(enchantment, requestedLevel);
		player.sendMessage(ChatColor.GREEN + "Successfully enchanted " + ChatColor.AQUA + itemInHand.getType().name().toLowerCase().replace('_', ' ') +
				ChatColor.GREEN + " with " + ChatColor.YELLOW + enchantment.getKey().getKey().replace('_', ' ') + " at level " + ChatColor.YELLOW + requestedLevel);
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		if (args.length == 1) {
			return Arrays.stream(Enchantment.values())
					.map(Enchantment::getName)
					.filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
					.collect(Collectors.toList());
		} else if (args.length == 2) {
			return Arrays.asList("1", "2", "3", "4", "5");
		}
		return null;
	}

}
