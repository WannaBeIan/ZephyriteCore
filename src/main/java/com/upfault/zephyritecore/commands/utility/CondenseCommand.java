package com.upfault.zephyritecore.commands.utility;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CondenseCommand implements CommandExecutor {

	private static final Map<Material, Material> condensibleItems = new HashMap<>();

	static {
		condensibleItems.put(Material.IRON_INGOT, Material.IRON_BLOCK);
		condensibleItems.put(Material.GOLD_INGOT, Material.GOLD_BLOCK);
		condensibleItems.put(Material.COPPER_INGOT, Material.COPPER_BLOCK);
		condensibleItems.put(Material.NETHERITE_INGOT, Material.NETHERITE_BLOCK);
		condensibleItems.put(Material.DIAMOND, Material.DIAMOND_BLOCK);
		condensibleItems.put(Material.EMERALD, Material.EMERALD_BLOCK);
		condensibleItems.put(Material.REDSTONE, Material.REDSTONE_BLOCK);
		condensibleItems.put(Material.LAPIS_LAZULI, Material.LAPIS_BLOCK);
		condensibleItems.put(Material.COAL, Material.COAL_BLOCK);
		condensibleItems.put(Material.QUARTZ, Material.QUARTZ_BLOCK);
		condensibleItems.put(Material.ANCIENT_DEBRIS, Material.NETHERITE_BLOCK);
		condensibleItems.put(Material.WHEAT, Material.HAY_BLOCK);
		condensibleItems.put(Material.SLIME_BALL, Material.SLIME_BLOCK);
		condensibleItems.put(Material.HONEY_BOTTLE, Material.HONEY_BLOCK);
		condensibleItems.put(Material.SNOWBALL, Material.SNOW_BLOCK);
		condensibleItems.put(Material.CLAY_BALL, Material.CLAY);
		condensibleItems.put(Material.MELON_SLICE, Material.MELON);
		condensibleItems.put(Material.AMETHYST_SHARD, Material.AMETHYST_BLOCK);
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can use this command!");
			return true;
		}

		Player player = (Player) sender;

		if (args.length > 0 && args[0].equalsIgnoreCase("all")) {
			condenseAll(player);
		} else {
			condenseHand(player);
		}
		return true;
	}

	private void condenseAll(Player player) {
		StringBuilder message = new StringBuilder();
		boolean condensed = false;

		for (Material ingot : condensibleItems.keySet()) {
			int amount = countItem(player, ingot);
			if (amount >= 9) {
				Material block = getCondensedForm(ingot);
				int blocks = amount / 9;
				int leftover = amount % 9;

				removeItems(player, ingot, amount);
				player.getInventory().addItem(new ItemStack(block, blocks));
				if (leftover > 0) {
					player.getInventory().addItem(new ItemStack(ingot, leftover));
				}

				message.append(ChatColor.YELLOW).append("- ")
						.append(ChatColor.GOLD).append(blocks).append(" ")
						.append(formatBlockName(block)).append(", ")
						.append(leftover).append(" Left Over\n");

				condensed = true;
			}
		}

		if (condensed) {
			player.sendMessage(ChatColor.GREEN + "Items condensed successfully:\n" + message);
		} else {
			player.sendMessage(ChatColor.RED + "No items available for condensing.");
		}
	}

	private void condenseHand(Player player) {
		ItemStack handItem = player.getInventory().getItemInMainHand();

		if (handItem.getType() == Material.AIR) {
			player.sendMessage(ChatColor.RED + "You are not holding any item.");
			return;
		}

		Material handMaterial = handItem.getType();

		if (!isCondensible(handMaterial)) {
			player.sendMessage(ChatColor.RED + "The item you're holding cannot be condensed.");
			return;
		}

		int amount = countItem(player, handMaterial);
		if (amount < 9) {
			player.sendMessage(ChatColor.RED + "You don't have enough items to condense.");
			return;
		}

		Material block = getCondensedForm(handMaterial);
		int blocks = amount / 9;
		int leftover = amount % 9;

		removeItems(player, handMaterial, amount);
		player.getInventory().addItem(new ItemStack(block, blocks));
		if (leftover > 0) {
			player.getInventory().addItem(new ItemStack(handMaterial, leftover));
		}

		player.sendMessage(ChatColor.GREEN + "" + blocks + " " + formatBlockName(block)
				+ ChatColor.GREEN + ", " + leftover + " Left Over.");
	}

	public static boolean isCondensible(Material material) {
		return condensibleItems.containsKey(material);
	}

	public static Material getCondensedForm(Material material) {
		return condensibleItems.get(material);
	}

	private int countItem(Player player, Material material) {
		int count = 0;
		for (ItemStack item : player.getInventory().getContents()) {
			if (item != null && item.getType() == material) {
				count += item.getAmount();
			}
		}
		return count;
	}

	private void removeItems(Player player, Material material, int amount) {
		for (ItemStack item : player.getInventory().getContents()) {
			if (item != null && item.getType() == material) {
				int currentAmount = item.getAmount();
				if (currentAmount <= amount) {
					amount -= currentAmount;
					item.setAmount(0);
				} else {
					item.setAmount(currentAmount - amount);
					break;
				}
			}
		}
	}

	private String formatBlockName(Material block) {
		String formattedName = block.name().toLowerCase().replace("_block", " Block").replace('_', ' ');
		String[] words = formattedName.split(" ");
		StringBuilder builder = new StringBuilder();
		for (String word : words) {
			builder.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
		}
		return builder.toString().trim();
	}
}
