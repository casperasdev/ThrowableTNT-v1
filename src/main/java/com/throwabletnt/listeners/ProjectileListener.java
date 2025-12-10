package com.throwabletnt.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Entity;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import com.throwabletnt.ThrowableTNTPlugin;
import com.throwabletnt.config.ConfigManager;
import com.throwabletnt.explosion.ExplosionHandler;

public class ProjectileListener implements Listener {

    private final ThrowableTNTPlugin plugin;
    private final ConfigManager config;
    private final ExplosionHandler explosionHandler;

    public ProjectileListener(ThrowableTNTPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.explosionHandler = new ExplosionHandler(plugin);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Snowball)) {
            return;
        }

        Snowball projectile = (Snowball) event.getEntity();

        if (projectile.getCustomName() == null) {
            return;
        }

        try {
            String[] data = projectile.getCustomName().split(":");
            if (data.length != 2) {
                return;
            }

            double power = Double.parseDouble(data[0]);
            double distance = Double.parseDouble(data[1]);

            Location hitLocation = projectile.getLocation();

            // Create explosion
            explosionHandler.createExplosion(hitLocation, power);

            // Play explosion sound
            if (config.soundsEnabled()) {
                try {
                    hitLocation.getWorld().playSound(
                            hitLocation,
                            config.getExplosionSound(),
                            config.getExplosionVolume(),
                            config.getExplosionPitch()
                    );
                } catch (IllegalArgumentException e) {
                    if (config.isDebugMode()) {
                        plugin.getLogger().warning("Invalid explosion sound: " + config.getExplosionSound());
                    }
                }
            }

            // Create particle effects
            if (config.particlesEnabled()) {
                try {
                    Particle smokeType = Particle.valueOf(config.getSmokeType());
                    hitLocation.getWorld().spawnParticle(
                            smokeType,
                            hitLocation,
                            config.getSmokeCount(),
                            0.5, 0.5, 0.5,
                            0.1
                    );
                } catch (IllegalArgumentException e) {
                    if (config.isDebugMode()) {
                        plugin.getLogger().warning("Invalid smoke type: " + config.getSmokeType());
                    }
                }
            }

            projectile.remove();

        } catch (Exception e) {
            if (config.isDebugMode()) {
                plugin.getLogger().warning("Error handling projectile hit: " + e.getMessage());
            }
        }
    }
}
