package com.corosus.coroutil.paper.command;

import com.corosus.coroutil.util.CULog;
import com.corosus.modconfig.ConfigMod;
import com.corosus.modconfig.CoroConfigRegistry;
import com.corosus.modconfig.ModConfigData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PaperCommandCoro implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("coroutil.admin")) {
            sender.sendMessage("§cYou do not have permission to manage CoroUtil configurations.");
            return true;
        }

        // Expected: /coro config common [reload|save|get|set ...]
        if (args.length < 3 || !args[0].equalsIgnoreCase("config") || !args[1].equalsIgnoreCase("common")) {
            sendHelp(sender, label);
            return true;
        }

        String action = args[2].toLowerCase();

        switch (action) {
            case "reload" -> {
                CULog.log("Reloading all mods common configurations from disk via Paper command");
                if (ConfigMod.instance() != null) {
                    ConfigMod.instance().reloadConfigs("common");
                }
                sender.sendMessage("§a[CoroUtil] Reloaded all common configs from disk.");
                return true;
            }
            case "save" -> {
                CULog.log("Saving all coro mods runtime configs to disk via Paper command");
                CoroConfigRegistry.instance().forceSaveAllFilesFromRuntimeSettings();
                sender.sendMessage("§a[CoroUtil] Saved all coro configs to disk.");
                return true;
            }
            case "get" -> {
                if (args.length < 5) {
                    sender.sendMessage("§eUsage: §f/" + label + " config common get <file_name> <setting_name>");
                    return true;
                }
                String fileName = fileToConfig(args[3]);
                ModConfigData configData = CoroConfigRegistry.instance().lookupFilePathToConfig.get(fileName);
                if (configData == null) {
                    sender.sendMessage("§c[CoroUtil] Unknown config file: " + args[3]);
                    return true;
                }
                String settingName = args[4];
                Object value = CoroConfigRegistry.instance().getField(configData.configID, settingName);
                if (value == null) {
                    sender.sendMessage("§c[CoroUtil] Unknown setting: " + settingName + " in " + args[3]);
                    return true;
                }
                sender.sendMessage("§e[CoroUtil] §f" + settingName + " = §a" + value + " §7in " + args[3]);
                return true;
            }
            case "set" -> {
                if (args.length < 6) {
                    sender.sendMessage("§eUsage: §f/" + label + " config common set <file_name> <setting_name> <value>");
                    return true;
                }
                String fileName = fileToConfig(args[3]);
                ModConfigData configData = CoroConfigRegistry.instance().lookupFilePathToConfig.get(fileName);
                if (configData == null) {
                    sender.sendMessage("§c[CoroUtil] Unknown config file: " + args[3]);
                    return true;
                }
                String settingName = args[4];
                // Join any remaining arguments as string value
                StringBuilder valBuilder = new StringBuilder(args[5]);
                for (int i = 6; i < args.length; i++) {
                    valBuilder.append(" ").append(args[i]);
                }
                String valueStr = valBuilder.toString();

                boolean result = CoroConfigRegistry.instance().updateField(configData.configID, settingName, valueStr);
                if (result) {
                    Object updatedVal = CoroConfigRegistry.instance().getField(configData.configID, settingName);
                    CoroConfigRegistry.instance().forceSaveAllFilesFromRuntimeSettings();
                    sender.sendMessage("§a[CoroUtil] Set " + settingName + " to " + updatedVal + " in " + args[3]);
                } else {
                    sender.sendMessage("§c[CoroUtil] Failed to set " + settingName + " to '" + valueStr + "'");
                }
                return true;
            }
            default -> {
                sendHelp(sender, label);
                return true;
            }
        }
    }

    private void sendHelp(CommandSender sender, String label) {
        sender.sendMessage("§6§l[CoroUtil Configuration Commands]");
        sender.sendMessage("§e/" + label + " config common reload §7- Reload all configs from disk");
        sender.sendMessage("§e/" + label + " config common save §7- Save all runtime configs to disk");
        sender.sendMessage("§e/" + label + " config common get <file> <setting> §7- Get setting value");
        sender.sendMessage("§e/" + label + " config common set <file> <setting> <val> §7- Update setting value");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("coroutil.admin")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return filterStartingWith("config", args[0]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("config")) {
            return filterStartingWith("common", args[1]);
        }
        if (args.length == 3 && args[1].equalsIgnoreCase("common")) {
            return filterList(List.of("reload", "save", "get", "set"), args[2]);
        }
        if (args.length == 4 && (args[2].equalsIgnoreCase("get") || args[2].equalsIgnoreCase("set"))) {
            return filterList(getConfigs(), args[3]);
        }
        if (args.length == 5 && (args[2].equalsIgnoreCase("get") || args[2].equalsIgnoreCase("set"))) {
            return filterList(getConfigSettings(args[3]), args[4]);
        }

        return Collections.emptyList();
    }

    private List<String> filterStartingWith(String candidate, String prefix) {
        if (candidate.toLowerCase().startsWith(prefix.toLowerCase())) {
            return List.of(candidate);
        }
        return Collections.emptyList();
    }

    private List<String> filterList(Iterable<String> candidates, String prefix) {
        List<String> result = new ArrayList<>();
        String lower = prefix.toLowerCase();
        for (String c : candidates) {
            if (c.toLowerCase().startsWith(lower)) {
                result.add(c);
            }
        }
        return result;
    }

    public static List<String> getConfigs() {
        return CoroConfigRegistry.instance().lookupFilePathToConfig.keySet().stream()
                .map(e -> e.replace("\\", "--").replace("/", "--"))
                .toList();
    }

    public static List<String> getConfigSettings(String configName) {
        ModConfigData modConfigData = CoroConfigRegistry.instance().lookupFilePathToConfig.get(fileToConfig(configName));
        if (modConfigData != null) {
            List<String> list = new ArrayList<>();
            list.addAll(modConfigData.valsString.keySet());
            list.addAll(modConfigData.valsInteger.keySet());
            list.addAll(modConfigData.valsDouble.keySet());
            list.addAll(modConfigData.valsBoolean.keySet());
            Collections.sort(list);
            return list;
        }
        return Collections.emptyList();
    }

    public static String fileToConfig(String str) {
        return str.replace("--", "\\");
    }
}
