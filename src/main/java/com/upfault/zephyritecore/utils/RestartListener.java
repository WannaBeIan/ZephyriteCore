package com.upfault.zephyritecore.utils;

import com.upfault.zephyritecore.ZephyriteCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestartListener {

	private static final String RESTART_URL = "http://localhost:3000/restart";
	private static final String EXPECTED_MESSAGE = "restarting";
	private boolean countdownStarted = false;

	public void startListening() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(ZephyriteCore.getPlugin(), () -> {
			try {
				URL url = new URL(RESTART_URL);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Accept", "application/json");

				int code = connection.getResponseCode();
				if (code == HttpURLConnection.HTTP_OK) {
					BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					StringBuilder response = new StringBuilder();
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
					in.close();

					if (response.toString().contains(EXPECTED_MESSAGE) && !countdownStarted) {
						startCountdown();
						countdownStarted = true;
					}
				} else {
					ZephyriteCore.getPlugin().getLogger().warning("Error: Server returned code " + code);
				}
			} catch (Exception e) {
				ZephyriteCore.getPlugin().getLogger().warning("Error checking restart message: " + e.getMessage());
			}
		}, 0L, 20L);
	}

	private void startCountdown() {

		Bukkit.broadcastMessage("§c[Important] §eThis proxy will restart soon: §bFor a game update §eYou have §a30 seconds §eto disconnect.");

		Bukkit.getScheduler().runTaskTimer(ZephyriteCore.getPlugin(), new Runnable() {
			int countdown = 30;



			@Override
			public void run() {

				if (countdown == 0) {
					Bukkit.getScheduler().runTask(ZephyriteCore.getPlugin(), () -> {
						for (Player player : Bukkit.getOnlinePlayers()) {
							player.kickPlayer("§cProxy Restarting. Try connecting again later!");
						}
					});
					countdownStarted = false;
					return;
				}


				if (countdown == 30 || countdown == 15 || countdown <= 10) {
					String subtitle = "§aFor a game update §7(in §e" + countdown + "s§7)";
					for (Player player : Bukkit.getOnlinePlayers()) {
						player.sendTitle("§e§lSERVER REBOOT!", subtitle, 0, 40, 0);
					}
				}

				countdown--;
			}

		}, 0L, 20L);
	}
}
