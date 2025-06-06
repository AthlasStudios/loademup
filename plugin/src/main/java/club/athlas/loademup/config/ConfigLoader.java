package club.athlas.loademup.config;

import club.athlas.loademup.LoadEmUp;
import club.athlas.loademup.api.utils.FileUtils;
import club.athlas.loademup.config.constant.ConfigKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConfigLoader {

    private final LoadEmUp plugin;

    private FileConfiguration config;
    private Messages messages;

    public ConfigLoader(LoadEmUp plugin) {
        this.plugin = plugin;
    }

    public ConfigLoader load() {
        plugin.saveDefaultConfig();

        config = plugin.getConfig();
        messages = initLang();

        return this;
    }

    public void reload() {
        plugin.reloadConfig();

        config = plugin.getConfig();
        messages = initLang();
    }

    @Contract(value = " -> new", pure = true)
    private @NotNull Messages initLang() {
        loadDefaultLang();

        String currentLang = getString(ConfigKey.LANG_FILE, "EN_us");
        return Messages.initFromFile(plugin, plugin.getAdventure(), "lang/" + currentLang + ".yml");
    }

    private void loadDefaultLang() {
        List<String> availableLang = List.of("EN_us", "IT_it");

        availableLang.forEach(defLang -> {
            String path = "lang/" + defLang + ".yml";
            if (!FileUtils.fileExists(plugin.getDataFolder().getAbsolutePath() + "/" + path)) plugin.saveResource(path, false);
        });
    }

    public String getString(@NotNull ConfigKey configKey, String defaultValue) {
        String s = config.getString(configKey.getPath());

        if (s == null || s.isEmpty()) {
            printInvalidValueWarning(configKey, String.valueOf(defaultValue), true);
            return defaultValue;
        }

        return s;
    }

    public boolean getBool(@NotNull ConfigKey configKey, boolean defaultValue) {
        if (!config.isSet(configKey.getPath())) {
            printInvalidValueWarning(configKey, String.valueOf(defaultValue), true);
            return defaultValue;
        }

        String stringValue = config.getString(configKey.getPath());
        if (!(stringValue != null && (stringValue.equalsIgnoreCase("true") || stringValue.equalsIgnoreCase("false")))) {
            printInvalidValueWarning(configKey, String.valueOf(defaultValue), false);
            return defaultValue;
        }

        return config.getBoolean(configKey.getPath());
    }

    public long getLong(@NotNull ConfigKey configKey, long defaultValue) {
        if (config.get(configKey.getPath()) == null) {
            printInvalidValueWarning(configKey, String.valueOf(defaultValue), true);
            return defaultValue;
        }

        try {
            String stringValue = config.getString(configKey.getPath());
            if (stringValue == null) {
                printInvalidValueWarning(configKey, String.valueOf(defaultValue), true);
                return defaultValue;
            }

            Long.parseLong(stringValue);
            return config.getLong(configKey.getPath());
        } catch (NumberFormatException e) {
            return defaultValue;
        }

    }

    public Messages getMessages() {
        return messages;
    }

    private void printInvalidValueWarning(@NotNull ConfigKey key, String defaultValue, boolean empty) {
        plugin.getLogger().warning("[Config] " + (empty ? "Empty" : "Invalid") + " value found in " + key.getPath() +" in config.yml! Using the default one -> " + defaultValue);
    }

}
