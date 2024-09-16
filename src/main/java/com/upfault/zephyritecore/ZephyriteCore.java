package com.upfault.zephyritecore;

import com.upfault.zephyritecore.api.scoreboard.LobbyScoreboard;
import com.upfault.zephyritecore.commands.ZephyriteCommand;
import com.upfault.zephyritecore.commands.utility.*;
import com.upfault.zephyritecore.events.*;
import com.upfault.zephyritecore.utils.DatabaseManager;
import com.upfault.zephyritecore.utils.RestartListener;
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

        new RestartListener().startListening();

        getServer().getScheduler().runTaskTimer(this, () -> {
            for (FastBoard board : LobbyScoreboard.boards.values()) {
                LobbyScoreboard.updateScoreboard(board);
            }
        }, 0, 40);
    }


    private void eventRegister() {
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        getServer().getPluginManager().registerEvents(new LobbyProtectionListener(), this);

        getServer().getMessenger().registerOutgoingPluginChannel(this, "velocity:player_info");
        getServer().getMessenger().registerIncomingPluginChannel(this, "velocity:player_info", new PlayerInfoResponseListener(databaseManager));

        getServer().getMessenger().registerOutgoingPluginChannel(this, "velocity:main");
        getServer().getMessenger().registerIncomingPluginChannel(this, "velocity:main", new PlayerInfoResponseListener(databaseManager));
    }


    private void commandRegister() {
        Map<String, Object[]> commandMap = new HashMap<>();

        commandMap.put("zephyrite", new Object[] {new ZephyriteCommand(this), new ZephyriteCommand(this)});
        commandMap.put("condense", new Object[] {new CondenseCommand(), null});
        commandMap.put("enchant", new Object[] {new EnchantCommand(), new EnchantCommand()});
        commandMap.put("ec", new Object[] {new EnderChestCommand(), null});
        commandMap.put("feed", new Object[] {new FeedCommand(), null});
        commandMap.put("fly", new Object[] {new FlyCommand(), null});
        commandMap.put("flyspeed", new Object[] {new FlySpeedCommand(), new FlySpeedCommand()});
        commandMap.put("gamemode", new Object[] {new GameModeCommand(), new GameModeCommand()});
        commandMap.put("hat", new Object[] {new HatCommand(), null});
        commandMap.put("itemgive", new Object[] {new ItemGiveCommand(), null});
        commandMap.put("itemrename", new Object[] {new RenameCommand(), null});
        commandMap.put("spawn", new Object[] {new SpawnCommand(), new SpawnCommand()});
        commandMap.put("speed", new Object[] {new SpeedCommand(), new SpeedCommand()});
        commandMap.put("suicide", new Object[] {new SuicideCommand(), null});
        commandMap.put("time", new Object[] {new TimeCommand(), new TimeCommand()});
        commandMap.put("top", new Object[] {new TopCommand(), null});
        commandMap.put("tpall", new Object[] {new TpAllCommand(), null});
        commandMap.put("tp", new Object[] {new TpCommand(), null});
        commandMap.put("weather", new Object[] {new WeatherCommand(), new WeatherCommand()});
        commandMap.put("whois", new Object[] {new WhoisCommand(), null});

        for (Map.Entry<String, Object[]> entry : commandMap.entrySet()) {
            String commandName = entry.getKey();
            CommandExecutor executor = (CommandExecutor) entry.getValue()[0];
            TabCompleter tabCompleter = (TabCompleter) entry.getValue()[1];

            Objects.requireNonNull(getCommand(commandName)).setExecutor(executor);

            if (tabCompleter != null) {
                Objects.requireNonNull(getCommand(commandName)).setTabCompleter(tabCompleter);
            }
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
