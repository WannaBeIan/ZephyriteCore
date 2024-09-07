package com.upfault.zephyritecore.utils;

import net.md_5.bungee.api.ChatColor;

public class ColorUtils {

	public static String applyGradient(String text, String startHex, String endHex) {
		StringBuilder gradientText = new StringBuilder();
		int length = text.length();

		java.awt.Color startColor = java.awt.Color.decode(startHex);
		java.awt.Color endColor = java.awt.Color.decode(endHex);

		for (int i = 0; i < length; i++) {
			double ratio = (double) i / (length - 1);


			int red = (int) (startColor.getRed() * (1 - ratio) + endColor.getRed() * ratio);
			int green = (int) (startColor.getGreen() * (1 - ratio) + endColor.getGreen() * ratio);
			int blue = (int) (startColor.getBlue() * (1 - ratio) + endColor.getBlue() * ratio);

			String hexColor = String.format("#%02x%02x%02x", red, green, blue);
			gradientText.append(ChatColor.of(hexColor)).append(text.charAt(i));
		}

		return gradientText.toString();
	}


}
