package com.upfault.zephyritecore.utils;

import net.md_5.bungee.api.ChatColor;

public class StringUtils {

	@SuppressWarnings("deprecation")
	public static String centerString(String text) {
		int textLength = ChatColor.stripColor(text).length();
		int padding = (50 - textLength) / 2;

		if (padding <= 0) {
			return text;
		}

		return " ".repeat(padding) +
				text;
	}
}
