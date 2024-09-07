package com.upfault.zephyritecore.events;

import com.upfault.zephyritecore.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

@SuppressWarnings("deprecation")
public class ServerPingListener implements Listener {

	@EventHandler
	public void onServerListPing(ServerListPingEvent event) {

		String line1 = ChatColor.DARK_PURPLE + "ZephyriteMC " + ChatColor.GOLD + "[1.7-1.21]";
		String line2 = ChatColor.AQUA + ChatColor.BOLD.toString() + "In Development" + ChatColor.GRAY + " | " + ChatColor.DARK_AQUA + ChatColor.BOLD + "BedWars 0.1.1";
		String centeredMotd = StringUtils.centerString(line1) + "\n" + StringUtils.centerString(line2);

		event.setMotd(centeredMotd);
	}
}
