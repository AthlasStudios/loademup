package club.athlas.loademup.config.base;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;

@SuppressWarnings("unused")
public class YamlFileHandle {

    private final File file;

    private FileConfiguration config;

    public YamlFileHandle(File file) {
        this.file = file;
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    @Contract("_, _ -> new")
    public static @NotNull YamlFileHandle fromPath(@NotNull Plugin plugin, String path) {
        if (!plugin.getDataFolder().exists()) {
            //noinspection ResultOfMethodCallIgnored
            plugin.getDataFolder().mkdir();
        }

        File file = new File(plugin.getDataFolder(), path);

        try {
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new YamlFileHandle(file);
    }

    @Contract("_, _, _ -> new")
    public static @NotNull YamlFileHandle fromResource(@NotNull Plugin plugin, @NotNull String resourcePath, String destinationPath) {
        if (!plugin.getDataFolder().exists()) {
            //noinspection ResultOfMethodCallIgnored
            plugin.getDataFolder().mkdir();
        }

        File outFile = new File(plugin.getDataFolder(), destinationPath);

        if (!outFile.exists()) {
            try (InputStream in = plugin.getResource(resourcePath)) {
                if (in == null) {
                    throw new IllegalArgumentException("Resource not found: " + resourcePath);
                }

                try (OutputStream out = new FileOutputStream(outFile)) {
                    byte[] buffer = new byte[1024];
                    int length;

                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to save resource to file: " + destinationPath, e);
            }
        }

        return new YamlFileHandle(outFile);
    }

    @Contract("_, _ -> new")
    public static @NotNull YamlFileHandle fromResource(@NotNull Plugin plugin, @NotNull String path) {
        return fromResource(plugin, path, path);
    }

    public YamlFileHandle createEmptySections(String @NotNull ... paths) {
        for (String path : paths) {
            if (config.isConfigurationSection(path)) continue;
            config.createSection(path);
        }

        save();
        return this;
    }

    public @NotNull ConfigurationSection getSection(@NotNull String path) {
        ConfigurationSection section = config.getConfigurationSection(path);
        if (section == null) return config.createSection(path);

        return section;
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public Configuration getConfig() {
        return config;
    }

    public File getFile() {
        return file;
    }

}
