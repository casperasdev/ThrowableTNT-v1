package com.throwabletnt.explosion;

import org.bukkit.Location;
import com.throwabletnt.ThrowableTNTPlugin;
import com.throwabletnt.config.ConfigManager;

public class ExplosionHandler {

    private final ThrowableTNTPlugin plugin;
    private final ConfigManager config;

    public ExplosionHandler(ThrowableTNTPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }

    public void createExplosion(Location location, double power) {
        float effectivePower = (float) (power * config.getRadiusMultiplier());

        location.getWorld().createExplosion(
                location.getX(),
                location.getY(),
                location.getZ(),
                effectivePower,
                false,
                config.breakBlocks()
        );
    }
}
