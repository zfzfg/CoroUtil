package com.corosus.coroutil.loader.paper;

import com.corosus.coroutil.paper.CoroUtilPaperPlugin;
import com.corosus.modconfig.ConfigMod;
import com.corosus.modconfig.CoroConfigRegistry;

import java.nio.file.Path;

/**
 * Paper/Purpur implementation of ConfigMod.
 */
public class ConfigModPaper extends ConfigMod {

    private final CoroUtilPaperPlugin plugin;

    public ConfigModPaper(CoroUtilPaperPlugin plugin) {
        this.plugin = plugin;
        this.init();
    }

    @Override
    public Path getConfigPath() {
        if (plugin != null) {
            return plugin.getDataFolder().toPath().resolve("config");
        }
        return Path.of("plugins", "CoroUtil", "config");
    }

    @Override
    public void reloadConfigs(String side) {
        for (var config : CoroConfigRegistry.instance().configs) {
            config.writeConfigFile(false);
            if (config.configInstance != null) {
                config.configInstance.hookUpdatedValues();
            }
        }
    }
}
