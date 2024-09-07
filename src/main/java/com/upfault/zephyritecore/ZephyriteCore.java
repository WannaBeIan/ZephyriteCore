package com.upfault.zephyritecore;

import com.upfault.zephyritecore.api.scoreboard.LobbyScoreboard;
import com.upfault.zephyritecore.commands.ZephyriteCommand;
import com.upfault.zephyritecore.commands.utility.*;
import com.upfault.zephyritecore.events.*;
import com.upfault.zephyritecore.utils.DatabaseManager;
import fr.mrmicky.fastboard.FastBoard;
import lombok.Getter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ZephyriteCore extends JavaPlugin {

    @Getter
    private static ZephyriteCore plugin;
    @Getter
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        plugin = this;

        saveDefaultConfig();
        reloadConfig();

        databaseManager = new DatabaseManager();

        commandRegister();
        eventRegister();

        getServer().getScheduler().runTaskTimer(this, () -> {
            for (FastBoard board : LobbyScoreboard.boards.values()) {
                LobbyScoreboard.updateScoreboard(board);
            }
        }, 0, 40);
    }


    private void eventRegister() {
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new ServerPingListener(), this);
        getServer().getPluginManager().registerEvents(new LobbyProtectionListener(), this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "velocity:player_info");
        getServer().getMessenger().registerIncomingPluginChannel(this, "velocity:player_info", new PlayerInfoResponseListener(databaseManager));
    }

    private void commandRegister() {
        Map<String, CommandExecutor> commandExecutors = new HashMap<>();
        commandExecutors.put("zephyrite", new ZephyriteCommand(this));
        commandExecutors.put("condense", new CondenseCommand());
        commandExecutors.put("enchant", new EnchantCommand());
        commandExecutors.put("ec", new EnderChestCommand());
        commandExecutors.put("feed", new FeedCommand());
        commandExecutors.put("fly", new FlyCommand());
        commandExecutors.put("flyspeed", new FlySpeedCommand());
        commandExecutors.put("gamemode", new GameModeCommand());
        commandExecutors.put("hat", new HatCommand());
        commandExecutors.put("itemgive", new ItemGiveCommand());
        commandExecutors.put("itemrename", new RenameCommand());
        commandExecutors.put("spawn", new SpawnCommand());
        commandExecutors.put("speed", new SpeedCommand());
        commandExecutors.put("suicide", new SuicideCommand());
        commandExecutors.put("time", new TimeCommand());
        commandExecutors.put("top", new TopCommand());
        commandExecutors.put("tpall", new TpAllCommand());
        commandExecutors.put("tp", new TpCommand());
        commandExecutors.put("weather", new WeatherCommand());
        commandExecutors.put("whois", new WhoisCommand());
        Map<String, TabCompleter> tabCompleters = new HashMap<>();
        tabCompleters.put("zephyrite", new ZephyriteCommand(this));
        tabCompleters.put("enchant", new EnchantCommand());
        tabCompleters.put("spawn", new SpawnCommand());
        tabCompleters.put("speed", new SpeedCommand());
        tabCompleters.put("flyspeed", new FlySpeedCommand());
        tabCompleters.put("time", new TimeCommand());
        tabCompleters.put("weather", new WeatherCommand());
        tabCompleters.put("gamemode", new GameModeCommand());


        for (Map.Entry<String, CommandExecutor> entry : commandExecutors.entrySet()) {
            Objects.requireNonNull(getCommand(entry.getKey())).setExecutor(entry.getValue());
        }

        for (Map.Entry<String, TabCompleter> entry : tabCompleters.entrySet()) {
            Objects.requireNonNull(getCommand(entry.getKey())).setTabCompleter(entry.getValue());
        }
    }

    @Override
    public void onDisable() {

        if (databaseManager != null) {
            databaseManager.closeConnection();
        }

        plugin = null;
    }
}
