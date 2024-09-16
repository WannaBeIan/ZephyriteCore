package com.upfault.zephyritecore.events;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.inventory.EquipmentSlot;

public class LobbyProtectionListener implements Listener {

	private static final String LOBBY_WORLD_NAME = "lobby";

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (isLobbyWorld(event.getPlayer().getWorld().getName())) {
			event.getPlayer().setGameMode(GameMode.ADVENTURE);
			event.getPlayer().teleport(new Location(Bukkit.getWorld(LOBBY_WORLD_NAME), -92.5, 31.0, 64.5, -90, 0));
		}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		Player player = (Player) event.getEntity();
		if (event.getEntity() instanceof Player && isLobbyWorld(player.getWorld().getName())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player player) {
			if (isLobbyWorld(player.getWorld().getName())) {
				event.setCancelled(true);
			}
		}
	}


	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (isLobbyWorld(event.getPlayer().getWorld().getName())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (isLobbyWorld(event.getPlayer().getWorld().getName())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (isLobbyWorld(event.getPlayer().getWorld().getName())) {
			if (event.getClickedBlock() != null && event.getHand() == EquipmentSlot.HAND) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (isLobbyWorld(event.getLocation().getWorld().getName())) {
			if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
				event.setCancelled(true);
			}
		}
	}


	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		World world = event.getWorld();
		if (isLobbyWorld(world.getName())) {
			event.setCancelled(event.getCause() != WeatherChangeEvent.Cause.PLUGIN && event.getCause() != WeatherChangeEvent.Cause.COMMAND);
		}
	}


	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && isLobbyWorld(event.getEntity().getWorld().getName())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (isLobbyWorld(event.getPlayer().getWorld().getName())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerPickupItem(EntityPickupItemEvent event) {
		Player player = (Player) event.getEntity();
		if (event.getEntity() instanceof Player && isLobbyWorld(player.getWorld().getName())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onTimeSkip(TimeSkipEvent event) {
		World world = event.getWorld();
		if (isLobbyWorld(world.getName())) {
			if (event.getSkipReason() == TimeSkipEvent.SkipReason.NIGHT_SKIP || event.getSkipReason() == TimeSkipEvent.SkipReason.CUSTOM) {
				event.setCancelled(false);
			} else {
				event.setCancelled(true);
				world.setTime(1000);
			}
		}
	}


	private boolean isLobbyWorld(String worldName) {
		return worldName.equalsIgnoreCase(LOBBY_WORLD_NAME);
	}

}
