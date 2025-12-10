package com.throwabletnt.util;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private int cooldownDuration;

    public CooldownManager(int cooldownDuration) {
        this.cooldownDuration = cooldownDuration;
    }

    public void setCooldownDuration(int duration) {
        this.cooldownDuration = duration;
    }

    public boolean isOnCooldown(Player player) {
        UUID uuid = player.getUniqueId();

        if (!cooldowns.containsKey(uuid)) {
            return false;
        }

        long timeLeft = (cooldowns.get(uuid) - System.currentTimeMillis()) / 1000;
        return timeLeft > 0;
    }

    public int getRemainingCooldown(Player player) {
        UUID uuid = player.getUniqueId();

        if (!cooldowns.containsKey(uuid)) {
            return 0;
        }

        long timeLeft = (cooldowns.get(uuid) - System.currentTimeMillis()) / 1000;
        return timeLeft > 0 ? (int) timeLeft : 0;
    }

    public void setCooldown(Player player) {
        UUID uuid = player.getUniqueId();
        cooldowns.put(uuid, System.currentTimeMillis() + (cooldownDuration * 1000L));
    }

    public void resetCooldown(Player player) {
        cooldowns.remove(player.getUniqueId());
    }
}
