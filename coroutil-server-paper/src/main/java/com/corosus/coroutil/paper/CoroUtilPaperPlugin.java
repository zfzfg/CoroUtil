package com.corosus.coroutil.paper;

import com.corosus.coroutil.config.ConfigCoroUtil;
import com.corosus.coroutil.loader.paper.ConfigModPaper;
import com.corosus.coroutil.paper.command.PaperCommandCoro;
import com.corosus.coroutil.util.CULog;
import com.corosus.modconfig.CoroConfigRegistry;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main JavaPlugin class for CoroUtil on Paper, Purpur, and Folia servers.
 */
public class CoroUtilPaperPlugin extends JavaPlugin {

    private static CoroUtilPaperPlugin instance;
    private ConfigModPaper configMod;

    public static CoroUtilPaperPlugin getInstance() {
        return instance;
    }

    public ConfigModPaper getConfigMod() {
        return configMod;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // Initialize Paper config loader and hook (automatically registers ConfigCoroUtil)
        configMod = new ConfigModPaper(this);

        // Notify registry that configs are loaded
        CoroConfigRegistry.instance().allModsConfigsLoadedAndRegisteredHook();

        // Register /coro command
        PluginCommand coroCmd = getCommand("coro");
        if (coroCmd != null) {
            PaperCommandCoro commandExecutor = new PaperCommandCoro();
            coroCmd.setExecutor(commandExecutor);
            coroCmd.setTabCompleter(commandExecutor);
        } else {
            getLogger().warning("Could not register '/coro' command from plugin.yml!");
        }

        getLogger().info("CoroUtil Paper Server Plugin v" + getDescription().getVersion() + " (Java 25) initialized successfully!");
    }

    @Override
    public void onDisable() {
        try {
            CULog.log("Saving all CoroUtil configs to disk upon server shutdown");
            CoroConfigRegistry.instance().forceSaveAllFilesFromRuntimeSettings();
        } catch (Exception ex) {
            getLogger().warning("Error while saving CoroUtil configs on shutdown: " + ex.getMessage());
        }

        configMod = null;
        instance = null;
    }
}
