package com.throwabletnt.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import com.throwabletnt.ThrowableTNTPlugin;
import com.throwabletnt.config.ConfigManager;
import com.throwabletnt.item.TNTItemBuilder;
import com.throwabletnt.util.CooldownManager;

public class PlayerInteractListener implements Listener {

    private final ThrowableTNTPlugin plugin;
    private final ConfigManager config;
    private final CooldownManager cooldownManager;

    public PlayerInteractListener(ThrowableTNTPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.cooldownManager = plugin.getCooldownManager();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (!event.getAction().isRightClick()) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!TNTItemBuilder.isThrownTNT(item)) {
            return;
        }

        if (!player.hasPermission("throwabletnt.throw")) {
            player.sendMessage(config.getMessage("no-permission"));
            return;
        }

        if (player.hasPermission("throwabletnt.cooldown.bypass") || !cooldownManager.isOnCooldown(player)) {
            throwTNT(player, item);
            if (!player.hasPermission("throwabletnt.cooldown.bypass")) {
                cooldownManager.setCooldown(player);
            }
        } else {
            int remaining = cooldownManager.getRemainingCooldown(player);
            if (config.showActionBar()) {
                player.sendActionBar(config.getMessage("cooldown-message", "remaining", String.valueOf(remaining)));
            }
        }

        event.setCancelled(true);
    }

    private void throwTNT(Player player, ItemStack item) {
        double power = TNTItemBuilder.getPower(item);
        double distance = TNTItemBuilder.getDistance(item);

        Snowball projectile = player.launchProjectile(Snowball.class);
        projectile.setVelocity(player.getLocation().getDirection().multiply(distance * 1.5));

        // Store power and distance in the projectile's metadata
        projectile.setCustomName(power + ":" + distance);
        projectile.setCustomNameVisible(false);

        // Play throw sound
        if (config.soundsEnabled()) {
            try {
                player.getWorld().playSound(
                        player.getLocation(),
                        config.getThrowSound(),
                        config.getThrowVolume(),
                        config.getThrowPitch()
                );
            } catch (IllegalArgumentException e) {
                if (config.isDebugMode()) {
                    plugin.getLogger().warning("Invalid throw sound: " + config.getThrowSound());
                }
            }
        }

        // Start projectile trail particles
        if (config.projectileTrailEnabled()) {
            startTrailParticles(projectile);
        }

        // Consume one item from stack
        if (item.getAmount() <= 1) {
            player.getInventory().setItemInMainHand(null);
        } else {
            item.setAmount(item.getAmount() - 1);
        }

        player.sendMessage(config.getMessage("throw-success"));
    }

    private void startTrailParticles(Snowball projectile) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (projectile.isDead() || !projectile.isValid()) {
                    cancel();
                    return;
                }

                try {
                    Particle trailType = Particle.valueOf(config.getTrailType());
                    projectile.getWorld().spawnParticle(
                            trailType,
                            projectile.getLocation(),
                            config.getTrailCount(),
                            0.1, 0.1, 0.1,
                            0.01
                    );
                } catch (IllegalArgumentException e) {
                    if (config.isDebugMode()) {
                        plugin.getLogger().warning("Invalid trail particle type: " + config.getTrailType());
                    }
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
