package com.upfault.zephyritecore.utils;

import com.upfault.zephyritecore.ZephyriteCore;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class RestartListener {
	public void startListening() {
		int port = 8080;
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			ZephyriteCore.getPlugin().getLogger().info("Plugin listening for restart notification on port " + port);

			while (!serverSocket.isClosed()) {
				try (Socket clientSocket = serverSocket.accept()) {
					handleRequest(clientSocket);
				} catch (IOException e) {
					ZephyriteCore.getPlugin().getLogger().warning(e.getLocalizedMessage());
				}
			}
		} catch (IOException e) {
			ZephyriteCore.getPlugin().getLogger().warning(e.getLocalizedMessage());
		}
	}

	private void handleRequest(Socket clientSocket) {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

			StringBuilder body = new StringBuilder();
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				body.append(inputLine);
				if (inputLine.isEmpty()) break;
			}

			if (body.toString().contains("Server restarting in 30 seconds...")) {
				startCountdown();
			}

			out.println("HTTP/1.1 200 OK");
			out.println("Content-Type: text/plain");
			out.println("Content-Length: 0");
			out.println();
		} catch (IOException e) {
			ZephyriteCore.getPlugin().getLogger().warning(e.getLocalizedMessage());
		}
	}

	private void startCountdown() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(ZephyriteCore.getPlugin(), new Runnable() {
			int countdown = 30;

			@Override
			public void run() {
				if (countdown > 0) {
					if (countdown == 30 || countdown == 15 || countdown <= 10) {
						Bukkit.getOnlinePlayers().forEach(player ->
								player.sendTitle("§e§lSERVER REBOOT!", "§aFor a game update" + "§7(in §e" + countdown + "s§7) ", 10, Integer.MAX_VALUE, 20));
					}
					countdown--;
				}
			}
		}, 0L, 20L);
	}
}
