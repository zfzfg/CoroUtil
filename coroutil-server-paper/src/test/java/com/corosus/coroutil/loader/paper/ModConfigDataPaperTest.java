package com.corosus.coroutil.loader.paper;

import com.corosus.coroutil.config.ConfigCoroUtil;
import com.corosus.modconfig.ConfigMod;
import com.corosus.modconfig.CoroConfigRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ModConfigDataPaperTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // Reset registry maps for isolation
        CoroConfigRegistry.instance().configs.clear();
        CoroConfigRegistry.instance().liveEditConfigs.clear();
        CoroConfigRegistry.instance().lookupFilePathToConfig.clear();
        CoroConfigRegistry.instance().lookupRegistryNameToConfig.clear();

        // Setup custom ConfigMod pointing to temp directory
        new ConfigMod() {
            @Override
            public Path getConfigPath() {
                return tempDir.resolve("config");
            }

            @Override
            public void reloadConfigs(String side) {
                for (var config : CoroConfigRegistry.instance().configs) {
                    config.writeConfigFile(false);
                }
            }
        };
    }

    @Test
    void testModConfigDataPaperWriteAndLoad() {
        ConfigCoroUtil configInstance = new ConfigCoroUtil();
        ModConfigDataPaper configData = new ModConfigDataPaper("CoroUtil/General", "coroutil_general", ConfigCoroUtil.class, configInstance);

        // Write config file to disk
        configData.writeConfigFile(false);

        Path expectedFile = tempDir.resolve("config").resolve("CoroUtil/General.toml");
        assertTrue(Files.exists(expectedFile), "Config file should have been created on disk: " + expectedFile);

        // Verify loaded defaults
        assertTrue(configData.getConfigBoolean("useLoggingLog"));
        assertFalse(configData.getConfigBoolean("useLoggingDebug"));

        // Update value
        configData.setConfig("useLoggingDebug", true);
        assertEquals(Boolean.TRUE, configData.getConfigBoolean("useLoggingDebug"));

        // Re-read file to verify persistence
        ModConfigDataPaper reloadedData = new ModConfigDataPaper("CoroUtil/General", "coroutil_general", ConfigCoroUtil.class, configInstance);
        reloadedData.writeConfigFile(false);
        assertTrue(reloadedData.getConfigBoolean("useLoggingDebug"), "Persisted value should be true after reload");
    }
}
