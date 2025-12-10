package com.throwabletnt.config;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.List;

public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration config;
    private File configFile;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void reloadConfig() {
        loadConfig();
    }

    // ===== TNT Settings =====
    public String getTNTItemName() {
        return config.getString("tnt.item-name", "&c&lThrowable TNT");
    }

    public List<String> getTNTMainLore() {
        return config.getStringList("tnt.main-lore");
    }

    public String getTNTMaterial() {
        return config.getString("tnt.material", "TNT");
    }

    // ===== Explosion Settings =====
    public double getDefaultPower() {
        return config.getDouble("explosion.default-power", 4.0);
    }

    public double getMinPower() {
        return config.getDouble("explosion.min-power", 1.0);
    }

    public double getMaxPower() {
        return config.getDouble("explosion.max-power", 10.0);
    }

    public double getDefaultDistance() {
        return config.getDouble("explosion.default-distance", 1.0);
    }

    public double getMinDistance() {
        return config.getDouble("explosion.min-distance", 0.5);
    }

    public double getMaxDistance() {
        return config.getDouble("explosion.max-distance", 3.0);
    }

    public boolean breakBlocks() {
        return config.getBoolean("explosion.break-blocks", true);
    }

    public double getRadiusMultiplier() {
        return config.getDouble("explosion.radius-multiplier", 1.0);
    }

    // ===== Particle Settings =====
    public boolean particlesEnabled() {
        return config.getBoolean("particles.enabled", true);
    }

    public String getSmokeType() {
        return config.getString("particles.smoke-type", "SMOKE");
    }

    public int getSmokeCount() {
        return config.getInt("particles.smoke-count", 30);
    }

    public boolean projectileTrailEnabled() {
        return config.getBoolean("particles.projectile-trail", true);
    }

    public String getTrailType() {
        return config.getString("particles.trail-type", "SMOKE");
    }

    public int getTrailCount() {
        return config.getInt("particles.trail-count", 3);
    }

    // ===== Sound Settings =====
    public boolean soundsEnabled() {
        return config.getBoolean("sounds.enabled", true);
    }

    public String getThrowSound() {
        return config.getString("sounds.throw-sound", "entity.snowball.throw");
    }

    public float getThrowVolume() {
        return (float) config.getDouble("sounds.throw-volume", 0.7);
    }

    public float getThrowPitch() {
        return (float) config.getDouble("sounds.throw-pitch", 1.0);
    }

    public String getExplosionSound() {
        return config.getString("sounds.explosion-sound", "entity.generic.explode");
    }

    public float getExplosionVolume() {
        return (float) config.getDouble("sounds.explosion-volume", 1.0);
    }

    public float getExplosionPitch() {
        return (float) config.getDouble("sounds.explosion-pitch", 1.0);
    }

    // ===== Cooldown Settings =====
    public int getCooldownDuration() {
        return config.getInt("cooldown.duration", 3);
    }

    public boolean showActionBar() {
        return config.getBoolean("cooldown.show-action-bar", true);
    }

    // ===== Messages =====
    public String getMessage(String path, String... replacements) {
        String message = config.getString("messages." + path, "");
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            message = message.replace("%" + replacements[i] + "%", replacements[i + 1]);
        }
        return translateColorCodes(message);
    }

    public boolean isDebugMode() {
        return config.getBoolean("debug", false);
    }

    private String translateColorCodes(String message) {
        return message.replace("&", "§");
    }
}
