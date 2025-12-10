package com.throwabletnt.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import com.throwabletnt.ThrowableTNTPlugin;
import com.throwabletnt.config.ConfigManager;
import java.util.ArrayList;
import java.util.List;

public class TNTItemBuilder {

    private static final String POWER_KEY = "tnt_power";
    private static final String DISTANCE_KEY = "tnt_distance";
    private final ThrowableTNTPlugin plugin;
    private final ConfigManager config;

    public TNTItemBuilder(ThrowableTNTPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }

    public ItemStack createTNT(double power, double distance) {
        Material material;
        try {
            material = Material.valueOf(config.getTNTMaterial().toUpperCase());
        } catch (IllegalArgumentException e) {
            material = Material.TNT;
        }
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        // Set display name with color codes
        String displayName = config.getTNTItemName();
        meta.setDisplayName(translateColorCodes(displayName));

        // Build lore with customizable main lore and power/distance info
        List<String> lore = buildLore(power, distance);
        meta.setLore(lore);

        // Store power and distance in NBT data (hidden from players)
        PersistentDataContainer data = meta.getPersistentDataContainer();
        NamespacedKey powerKey = new NamespacedKey(plugin, POWER_KEY);
        NamespacedKey distanceKey = new NamespacedKey(plugin, DISTANCE_KEY);

        data.set(powerKey, PersistentDataType.DOUBLE, power);
        data.set(distanceKey, PersistentDataType.DOUBLE, distance);

        item.setItemMeta(meta);
        return item;
    }

    private List<String> buildLore(double power, double distance) {
        List<String> lore = new ArrayList<>();

        // Get the customizable main lore from config
        List<String> mainLore = config.getTNTMainLore();
        for (String loreLine : mainLore) {
            String line = loreLine
                    .replace("%power%", String.format("%.1f", power))
                    .replace("%distance%", String.format("%.1f", distance));
            lore.add(translateColorCodes(line));
        }

        return lore;
    }

    public static double getPower(ItemStack item) {
        if (item == null || item.getItemMeta() == null) {
            return 0;
        }

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        NamespacedKey powerKey = new NamespacedKey(ThrowableTNTPlugin.getInstance(), POWER_KEY);

        Double power = data.get(powerKey, PersistentDataType.DOUBLE);
        return power != null ? power : 0;
    }

    public static double getDistance(ItemStack item) {
        if (item == null || item.getItemMeta() == null) {
            return 0;
        }

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        NamespacedKey distanceKey = new NamespacedKey(ThrowableTNTPlugin.getInstance(), DISTANCE_KEY);

        Double distance = data.get(distanceKey, PersistentDataType.DOUBLE);
        return distance != null ? distance : 0;
    }

    public static boolean isThrownTNT(ItemStack item) {
        if (item == null || item.getItemMeta() == null) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        NamespacedKey powerKey = new NamespacedKey(ThrowableTNTPlugin.getInstance(), POWER_KEY);

        return data.has(powerKey, PersistentDataType.DOUBLE);
    }

    private String translateColorCodes(String text) {
        return text.replace("&", "§");
    }
}
