package club.athlas.loademup.server.loader;

import club.athlas.loademup.LoadEmUp;
import club.athlas.loademup.api.exceptions.PluginAlreadyLoadedException;
import club.athlas.loademup.api.exceptions.PluginFileNotFoundException;
import club.athlas.loademup.api.loader.PluginLoader;
import club.athlas.loademup.api.loader.Simulator;
import club.athlas.loademup.api.object.Dependency;
import club.athlas.loademup.api.utils.FileUtils;
import club.athlas.loademup.server.utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.PluginClassLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public abstract class BasePluginLoader implements PluginLoader {

    protected final LoadEmUp plugin;
    protected final PluginManager pluginManager;
    protected final Simulator simulatedPluginLoader;

    protected BasePluginLoader(@NotNull LoadEmUp plugin, PluginManager pluginManager) {
        this.plugin = plugin;
        this.pluginManager = pluginManager;
        this.simulatedPluginLoader = new SimulatedPluginLoader(this);
    }

    protected BasePluginLoader(@NotNull LoadEmUp plugin) {
        this(plugin, plugin.getServer().getPluginManager());
    }

    @Override
    public Optional<Plugin> load(File file) throws InvalidPluginException, InvalidDescriptionException {
        if (isLoaded(file)) throw new PluginAlreadyLoadedException(file);

        Plugin loadedPlugin = pluginManager.loadPlugin(file);

        if (loadedPlugin == null) return Optional.empty();

        loadedPlugin.onLoad();
        enablePlugin(loadedPlugin);

        syncCommands();

        return Optional.of(loadedPlugin);
    }

    @Override
    public Optional<Plugin> load(Plugin plugin) throws InvalidPluginException, InvalidDescriptionException {
        Optional<File> fileOptional = getPluginFile(plugin);

        if (fileOptional.isEmpty()) {
            return Optional.empty();
        }

        return load(fileOptional.get());
    }

    @Override
    public Optional<Plugin> reload(Plugin plugin) throws InvalidPluginException, InvalidDescriptionException {
        if (getPluginFile(plugin).isEmpty()) throw new PluginFileNotFoundException(plugin);

        unload(plugin);
        return load(plugin);
    }

    @Override
    public boolean isLoaded(File file) {
        return getDescriptionFile(file)
                .map(description -> pluginManager.getPlugin(description.getName()) != null)
                .orElse(false);
    }

    @Override
    public void disablePlugin(@NotNull Plugin plugin) {
        if (!plugin.isEnabled()) return;
        pluginManager.disablePlugin(plugin);
    }

    @Override
    public void enablePlugin(@NotNull Plugin plugin) {
        if (plugin.isEnabled()) return;
        pluginManager.enablePlugin(plugin);
    }

    @Override
    public boolean isEnabled(@NotNull Plugin plugin) {
        return plugin.isEnabled();
    }

    @Override
    public void reloadConfig(@NotNull Plugin plugin) {
        if (!plugin.isEnabled()) return;
        plugin.reloadConfig();
    }

    @Override
    public Optional<Plugin> getPlugin(@NotNull String name) {
        return Optional.ofNullable(pluginManager.getPlugin(name));
    }

    @Override
    public Set<Dependency> getMissingDependencies(@NotNull PluginDescriptionFile description) {
        return getDependencies(description).stream()
                .filter(dependency -> dependency.required() && !dependency.isLoaded())
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Dependency> getMissingDependencies(@NotNull File file) {
        return getDescriptionFile(file)
                .map(this::getMissingDependencies)
                .orElse(Collections.emptySet());
    }

    @Override
    public Set<Dependency> getDependencies(@NotNull File file) {
        return getDescriptionFile(file)
                .map(this::getDependencies)
                .orElse(Collections.emptySet());
    }

    @Override
    public Optional<PluginDescriptionFile> getDescriptionFile(File file) {
        if (FileUtils.isJarFile(file)) return Optional.empty();

        try (JarFile jar = new JarFile(file)) {
            JarEntry entry = jar.getJarEntry("plugin.yml");
            if (entry == null) return Optional.empty();

            try (InputStream is = jar.getInputStream(entry)) {
                return Optional.of(new PluginDescriptionFile(is));
            } catch (InvalidDescriptionException e) {
                return Optional.empty();
            }
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public Optional<File> getPluginFile(Plugin plugin) {
        try {
            ClassLoader classLoader = plugin.getClass().getClassLoader();

            if (!(classLoader instanceof PluginClassLoader pluginClassLoader)) return Optional.empty();

            File file = ReflectionUtils.accessField(pluginClassLoader, "file");
            if (file == null || !file.exists()) return Optional.empty();

            return Optional.of(file);

        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<File> getPluginFile(String name) {
        File pluginFile = new File(getPluginsFolder(), name);
        return pluginFile.exists() ? Optional.of(pluginFile) : Optional.empty();
    }

    @Override
    public Set<File> getPluginJarFiles() {
        return Arrays.stream(Objects.requireNonNull(getPluginsFolder().listFiles()))
                .filter(File::isFile)
                .filter(file -> FileUtils.isJarFile(file) && getDescriptionFile(file).isPresent())
                .collect(Collectors.toSet());
    }

    @Override
    public @NotNull File getPluginsFolder() {
        return plugin.getDataFolder().getParentFile();
    }

    @Override
    public @NotNull Simulator getSimulatedPluginLoader() {
        return simulatedPluginLoader;
    }

    protected void syncCommands() {
        ((CraftServer) Bukkit.getServer()).syncCommands();
    }

    protected void closeClassLoader(@NotNull Plugin plugin) {
        try {
            if (plugin.getClass().getClassLoader() instanceof URLClassLoader urlClassLoader) urlClassLoader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
