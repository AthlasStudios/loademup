package club.athlas.loademup.api.loader;

import club.athlas.loademup.api.object.Dependency;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Optional;
import java.util.Set;

public interface PluginLoader {

    Optional<Plugin> load(File file) throws InvalidPluginException, InvalidDescriptionException;

    Optional<Plugin> load(Plugin plugin) throws InvalidPluginException, InvalidDescriptionException;

    void unload(Plugin plugin);

    Optional<Plugin> reload(Plugin plugin) throws InvalidPluginException, InvalidDescriptionException;

    boolean isLoaded(File file);

    void disablePlugin(@NotNull Plugin plugin);

    void enablePlugin(@NotNull Plugin plugin);

    boolean isEnabled(@NotNull Plugin plugin);

    void reloadConfig(@NotNull Plugin plugin);

    Optional<Plugin> getPlugin(@NotNull String name);

    Set<Dependency> getMissingDependencies(@NotNull PluginDescriptionFile description);

    Set<Dependency> getMissingDependencies(@NotNull File file);

    Set<Dependency> getDependencies(@NotNull PluginDescriptionFile description);

    Set<Dependency> getDependencies(@NotNull File file);

    Optional<PluginDescriptionFile> getDescriptionFile(File file);

    Optional<File> getPluginFile(Plugin plugin);

    Optional<File> getPluginFile(String name);

    Set<File> getPluginJarFiles();

    @NotNull File getPluginsFolder();

    @NotNull Simulator getSimulatedPluginLoader();

}
