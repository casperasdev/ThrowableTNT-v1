package com.throwabletnt.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.inventory.ItemStack;
import com.throwabletnt.ThrowableTNTPlugin;
import com.throwabletnt.config.ConfigManager;
import com.throwabletnt.item.TNTItemBuilder;
import java.util.ArrayList;
import java.util.List;

public class ThrowableTNTCommand implements CommandExecutor, TabCompleter {

    private final ThrowableTNTPlugin plugin;
    private final ConfigManager config;
    private final TNTItemBuilder itemBuilder;

    public ThrowableTNTCommand(ThrowableTNTPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.itemBuilder = new TNTItemBuilder(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "give":
                return handleGive(sender, args);
            case "reload":
                return handleReload(sender);
            case "info":
                return handleInfo(sender);
            case "cooldown":
                return handleCooldown(sender, args);
            case "help":
                return handleHelp(sender);
            default:
                sendHelp(sender);
                return true;
        }
    }

    private boolean handleGive(CommandSender sender, String[] args) {
        if (!sender.hasPermission("throwabletnt.give")) {
            sender.sendMessage(config.getMessage("no-permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /throwabletnt give <player> <amount> [power] [distance]");
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(args[1]);
        if (targetPlayer == null) {
            sender.sendMessage(config.getMessage("invalid-player", "player", args[1]));
            return true;
        }

        int amount = parseInteger(args, 2, 1);
        double power = parseDouble(args, 3, config.getDefaultPower());
        double distance = parseDouble(args, 4, config.getDefaultDistance());

        // Validate power
        if (power < config.getMinPower() || power > config.getMaxPower()) {
            sender.sendMessage(config.getMessage("invalid-power",
                    "min", String.valueOf(config.getMinPower()),
                    "max", String.valueOf(config.getMaxPower())));
            return true;
        }

        // Validate distance
        if (distance < config.getMinDistance() || distance > config.getMaxDistance()) {
            sender.sendMessage(config.getMessage("invalid-distance",
                    "min", String.valueOf(config.getMinDistance()),
                    "max", String.valueOf(config.getMaxDistance())));
            return true;
        }

        // Create and give items
        ItemStack tntItem = itemBuilder.createTNT(power, distance);
        tntItem.setAmount(amount);
        targetPlayer.getInventory().addItem(tntItem);

        sender.sendMessage(config.getMessage("give-success",
                "player", targetPlayer.getName(),
                "amount", String.valueOf(amount),
                "power", String.format("%.1f", power),
                "distance", String.format("%.1f", distance)));

        return true;
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("throwabletnt.reload")) {
            sender.sendMessage(config.getMessage("no-permission"));
            return true;
        }

        config.reloadConfig();
        plugin.getCooldownManager().setCooldownDuration(config.getCooldownDuration());
        sender.sendMessage(config.getMessage("config-reloaded"));
        return true;
    }

    private boolean handleInfo(CommandSender sender) {
        sender.sendMessage("§c§l=== ThrowableTNT Plugin Info ===");
        sender.sendMessage("§7Version: §e1.0.0");
        sender.sendMessage("§7Default Power: §e" + config.getDefaultPower());
        sender.sendMessage("§7Default Distance: §e" + config.getDefaultDistance());
        sender.sendMessage("§7Cooldown: §e" + config.getCooldownDuration() + "s");
        sender.sendMessage("§7Particles Enabled: §e" + config.particlesEnabled());
        sender.sendMessage("§7Sounds Enabled: §e" + config.soundsEnabled());
        return true;
    }

    private boolean handleCooldown(CommandSender sender, String[] args) {
        if (!sender.hasPermission("throwabletnt.reload")) {
            sender.sendMessage(config.getMessage("no-permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /throwabletnt cooldown <player>");
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(args[1]);
        if (targetPlayer == null) {
            sender.sendMessage(config.getMessage("invalid-player", "player", args[1]));
            return true;
        }

        plugin.getCooldownManager().resetCooldown(targetPlayer);
        sender.sendMessage("§aReset cooldown for " + targetPlayer.getName());
        return true;
    }

    private boolean handleHelp(CommandSender sender) {
        sendHelp(sender);
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§c§l=== ThrowableTNT Commands ===");
        sender.sendMessage("§e/throwabletnt give <player> <amount> [power] [distance]§7 - Give TNT to a player");
        sender.sendMessage("§e/throwabletnt reload§7 - Reload configuration");
        sender.sendMessage("§e/throwabletnt info§7 - Show plugin information");
        sender.sendMessage("§e/throwabletnt cooldown <player>§7 - Reset player cooldown");
        sender.sendMessage("§e/throwabletnt help§7 - Show this message");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("give");
            completions.add("reload");
            completions.add("info");
            completions.add("cooldown");
            completions.add("help");
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("cooldown"))) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            completions.add("1");
            completions.add("64");
        } else if (args.length == 4 && args[0].equalsIgnoreCase("give")) {
            completions.add(String.valueOf(config.getDefaultPower()));
        } else if (args.length == 5 && args[0].equalsIgnoreCase("give")) {
            completions.add(String.valueOf(config.getDefaultDistance()));
        }

        return completions;
    }

    private int parseInteger(String[] args, int index, int defaultValue) {
        if (index >= args.length) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(args[index]);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private double parseDouble(String[] args, int index, double defaultValue) {
        if (index >= args.length) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(args[index]);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
