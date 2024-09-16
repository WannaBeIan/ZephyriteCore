package com.upfault.zephyritecore.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Objects;

public class ScoreboardUtils {
	public static int totalPlayers = 0;

	public static void requestTotalPlayers() {
		if (Bukkit.getOnlinePlayers().isEmpty()) {
			return;
		}

		Player player = Bukkit.getOnlinePlayers().iterator().next();

		try (ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
			 DataOutputStream out = new DataOutputStream(byteArrayStream)) {

			out.writeUTF("PlayerCount");
			out.writeUTF("ALL");

			player.sendPluginMessage(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("ZephyriteCore")),
					"velocity:main", byteArrayStream.toByteArray());

		} catch (Exception e) {
			Bukkit.getLogger().warning("Failed to request total player count: " + e.getMessage());
		}
	}
}
