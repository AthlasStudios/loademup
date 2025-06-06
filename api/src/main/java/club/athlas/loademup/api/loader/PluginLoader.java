package club.athlas.loademup.api.loader;

import club.athlas.loademup.api.exceptions.PluginAlreadyLoadedException;
import club.athlas.loademup.api.exceptions.PluginFileNotFoundException;
import club.athlas.loademup.api.object.Dependency;
import club.athlas.loademup.api.result.ClassLoaderValidationResult;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PluginLoader {

    Optional<Plugin> load(File file) throws InvalidPluginException, InvalidDescriptionException, PluginAlreadyLoadedException;

    Optional<Plugin> load(Plugin plugin) throws InvalidPluginException, InvalidDescriptionException, PluginAlreadyLoadedException;

    void unload(Plugin plugin);

    Optional<Plugin> reload(Plugin plugin) throws InvalidPluginException, InvalidDescriptionException, PluginFileNotFoundException;

    boolean isLoaded(File file);

    void disablePlugin(@NotNull Plugin plugin);

    void enablePlugin(@NotNull Plugin plugin);

    boolean isEnabled(@NotNull Plugin plugin);

    void reloadConfig(@NotNull Plugin plugin);

    Set<Plugin> getPlugins();

    Optional<Plugin> getPlugin(@NotNull String name);

    List<Dependency> getDependents(@NotNull Plugin plugin);

    List<Dependency> getMissingDependencies(@NotNull PluginDescriptionFile description);

    List<Dependency> getMissingDependencies(@NotNull File file);

    List<Dependency> getDependencies(@NotNull PluginDescriptionFile description);

    List<Dependency> getDependencies(@NotNull Plugin plugin);

    List<Dependency> getDependencies(@NotNull File file);

    Optional<PluginDescriptionFile> getDescriptionFile(File file);

    Optional<File> getPluginFile(Plugin plugin);

    Optional<File> getPluginFile(String name);

    Set<File> getPluginJarFiles();

    @NotNull File getPluginsFolder();

    ClassLoaderValidationResult validateClassLoader(File file);
}
