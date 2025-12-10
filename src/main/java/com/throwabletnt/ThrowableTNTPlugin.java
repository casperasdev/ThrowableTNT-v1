package com.throwabletnt;

import org.bukkit.plugin.java.JavaPlugin;
import com.throwabletnt.config.ConfigManager;
import com.throwabletnt.commands.ThrowableTNTCommand;
import com.throwabletnt.listeners.ProjectileListener;
import com.throwabletnt.listeners.PlayerInteractListener;
import com.throwabletnt.util.CooldownManager;

public class ThrowableTNTPlugin extends JavaPlugin {

    private static ThrowableTNTPlugin instance;
    private ConfigManager configManager;
    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize configuration
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        // Initialize cooldown manager
        cooldownManager = new CooldownManager(configManager.getCooldownDuration());

        // Register commands
        getCommand("throwabletnt").setExecutor(new ThrowableTNTCommand(this));
        getCommand("throwabletnt").setTabCompleter(new ThrowableTNTCommand(this));

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new ProjectileListener(this), this);

        getLogger().info("ThrowableTNT plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ThrowableTNT plugin disabled!");
    }

    public static ThrowableTNTPlugin getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
}
