package com.corosus.coroutil.loader.fabric;

import com.corosus.coroutil.util.CULog;
import com.corosus.modconfig.ConfigComment;
import com.corosus.modconfig.ConfigParams;
import com.corosus.modconfig.CoroConfigRegistry;
import com.corosus.modconfig.IConfigCategory;
import com.corosus.modconfig.ModConfigData;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

/** Fabric TOML backend. Keeps the same general.<field> layout used by Forge config files. */
public class ModConfigDataFabric extends ModConfigData {

    private final HashMap<String, Object> values = new HashMap<>();
    private Path configFile;

    public ModConfigDataFabric(String savePath, String parStr, Class parClass, IConfigCategory parConfig) {
        super(savePath, parStr, parClass, parConfig);
    }

    @Override
    public synchronized void writeConfigFile(boolean resetConfig) {
        try {
            configFile = FabricLoader.getInstance().getConfigDir().resolve(saveFilePath + ".toml");
            Files.createDirectories(configFile.getParent());
            try (CommentedFileConfig config = CommentedFileConfig.builder(configFile)
                    .preserveInsertionOrder()
                    .sync()
                    .writingMode(WritingMode.REPLACE)
                    .build()) {
                config.load();
                CommentedConfig general = config.getOrElse("general", CommentedConfig::inMemory);
                for (Field field : configClass.getDeclaredFields()) {
                    String name = field.getName();
                    Object defaultValue = CoroConfigRegistry.instance().getField(configID, name);
                    if (!(defaultValue instanceof String || defaultValue instanceof Integer || defaultValue instanceof Double || defaultValue instanceof Boolean)) continue;
                    String comment = "-";
                    ConfigComment configComment = field.getAnnotation(ConfigComment.class);
                    if (configComment != null && configComment.value().length > 0) comment = configComment.value()[0];
                    ConfigParams params = field.getAnnotation(ConfigParams.class);
                    if (params != null) comment = params.comment();
                    Object value = general.contains(name) ? general.get(name) : defaultValue;
                    value = normalizeAndClamp(value, defaultValue, params);
                    general.set(name, value);
                    general.setComment(name, comment);
                    values.put(name, value);
                    setFieldBasedOnType(name, value);
                }
                config.set("general", general);
                config.save();
            }
            CULog.dbg("writeConfigFile invoked for " + configID + ", resetConfig: " + resetConfig);
        } catch (Exception exception) {
            throw new RuntimeException("Could not read or save CoroUtil config " + saveFilePath, exception);
        }
    }

    private Object normalizeAndClamp(Object value, Object defaultValue, ConfigParams params) {
        if (defaultValue instanceof String) return value instanceof String ? value : defaultValue;
        if (defaultValue instanceof Boolean) return value instanceof Boolean ? value : defaultValue;
        try {
            double number = value instanceof Number ? ((Number) value).doubleValue() : Double.parseDouble(String.valueOf(value));
            double min = params == null ? -Double.MAX_VALUE : params.min();
            double max = params == null ? Double.MAX_VALUE : params.max();
            number = Math.max(min, Math.min(max, number));
            if (defaultValue instanceof Integer) return (int) number;
            return number;
        } catch (RuntimeException exception) {
            return defaultValue;
        }
    }

    private synchronized void save(String fieldName, Object value) {
        if (configFile == null) return;
        try (CommentedFileConfig config = CommentedFileConfig.builder(configFile).sync().build()) {
            config.load();
            CommentedConfig general = config.get("general");
            if (general == null) {
                general = CommentedConfig.inMemory();
                config.set("general", general);
            }
            general.set(fieldName, value);
            config.save();
            values.put(fieldName, value);
        }
    }

    @Override public String getConfigString(String name) { return (String) values.get(name); }
    @Override public Integer getConfigInteger(String name) { return (Integer) values.get(name); }
    @Override public Double getConfigDouble(String name) { return (Double) values.get(name); }
    @Override public Boolean getConfigBoolean(String name) { return (Boolean) values.get(name); }
    @Override public <T> void setConfig(String name, T value) { save(name, value); }
}
