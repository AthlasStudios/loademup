package club.athlas.loademup.server.loader;

import club.athlas.loademup.LoadEmUp;
import club.athlas.loademup.api.exceptions.PluginAlreadyLoadedException;
import club.athlas.loademup.api.exceptions.PluginFileNotFoundException;
import club.athlas.loademup.api.loader.PluginLoader;
import club.athlas.loademup.api.object.Dependency;
import club.athlas.loademup.api.result.ClassLoaderValidationResult;
import club.athlas.loademup.api.utils.FileUtils;
import club.athlas.loademup.utils.ReflectionUtils;
import club.athlas.loademup.utils.ThreadUtils;
import club.athlas.loademup.server.wrapper.WrappedInstanceManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.PluginClassLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.stream.Collectors;

public abstract class BasePluginLoader implements PluginLoader {

    protected final LoadEmUp plugin;
    protected final PluginManager pluginManager;


    protected BasePluginLoader(@NotNull LoadEmUp plugin, PluginManager pluginManager) {
        this.plugin = plugin;
        this.pluginManager = pluginManager;
    }

    protected BasePluginLoader(@NotNull LoadEmUp plugin) {
        this(plugin, plugin.getServer().getPluginManager());
    }

    @Override
    public Optional<Plugin> load(File file) throws InvalidPluginException, InvalidDescriptionException, PluginAlreadyLoadedException {
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
    public Optional<Plugin> reload(Plugin plugin) throws InvalidPluginException, InvalidDescriptionException, PluginFileNotFoundException {
        if (getPluginFile(plugin).isEmpty()) throw new PluginFileNotFoundException(plugin);

        unload(plugin);
        return load(plugin);
    }

    @Override
    public Optional<Plugin> reloadFromCurrentJar(Plugin plugin) throws InvalidPluginException, InvalidDescriptionException, PluginFileNotFoundException {
        Optional<File> currentJar = getPluginCurrentJarInst(plugin);
        if (currentJar.isEmpty()) throw new PluginFileNotFoundException(plugin);

        unload(plugin);
        return load(currentJar.get());
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

        stopPluginThreads(plugin);
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
    public Set<Plugin> getPlugins() {
        return Set.of(pluginManager.getPlugins());
    }

    @Override
    public Optional<Plugin> getPlugin(@NotNull String name) {
        return Optional.ofNullable(pluginManager.getPlugin(name));
    }

    @Override
    public List<Dependency> getMissingDependencies(@NotNull PluginDescriptionFile description) {
        return getDependencies(description).stream()
                .filter(dependency -> dependency.required() && !dependency.isLoaded())
                .collect(Collectors.toList());
    }

    @Override
    public List<Dependency> getMissingDependencies(@NotNull File file) {
        return getDescriptionFile(file)
                .map(this::getMissingDependencies)
                .orElse(Collections.emptyList());
    }

    @Override
    public List<Dependency> getDependencies(@NotNull File file) {
        return getDescriptionFile(file)
                .map(this::getDependencies)
                .orElse(Collections.emptyList());
    }

    @Override
    public Optional<PluginDescriptionFile> getDescriptionFile(File file) {
        if (!FileUtils.isJarFile(file)) return Optional.empty();

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

    @Override
    public Optional<File> getPluginCurrentJarInst(Plugin plugin) {
        try {
            URL jarUrl = plugin.getClass()
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation();

            if (jarUrl == null) return Optional.empty();

            return Optional.of(new File(jarUrl.toURI()));

        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get current jar", e);
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
            if (file == null) return Optional.empty();

            return Optional.of(file);

        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<File> getPluginFile(String name) {
        try {
            File pluginFile = new File(getPluginsFolder(), name);
            return pluginFile.exists() ? Optional.of(pluginFile) : Optional.empty();
        } catch (NullPointerException e) {
            return Optional.empty();
        }
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
        return plugin.getServer().getPluginsFolder();
    }

    @Override
    public ClassLoaderValidationResult validateClassLoader(File file) {
        Optional<PluginDescriptionFile> descOpt = getDescriptionFile(file);

        if (descOpt.isEmpty()) return ClassLoaderValidationResult.MISSING_PLUGIN_DESCRIPTION;
        PluginDescriptionFile desc = descOpt.get();

        try (URLClassLoader testLoader = new URLClassLoader(
                new URL[] { file.toURI().toURL() }, Bukkit.class.getClassLoader())) {

            Class<?> mainClass = testLoader.loadClass(desc.getMain());

            if (!Plugin.class.isAssignableFrom(mainClass)) {
                return ClassLoaderValidationResult.INVALID_MAIN_CLASS;
            }

            try {
                mainClass.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                return ClassLoaderValidationResult.INVALID_MAIN_CLASS;
            }

        } catch (MalformedURLException e) {
            return ClassLoaderValidationResult.FILE_NOT_FOUND;
        } catch (ClassNotFoundException e) {
            return ClassLoaderValidationResult.NO_MAIN_CLASS;
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to close test class loader", e);
            return ClassLoaderValidationResult.UNEXPECTED_ERROR;
        }

        return ClassLoaderValidationResult.VALID;
    }

    protected void syncCommands() {
        try {
            ReflectionUtils.callMethod(Bukkit.getServer(), "syncCommands");
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {

        }
    }

    protected void cleanup(@NotNull WrappedInstanceManager instanceManager, Plugin plugin) {
        Map<String, Command> knownCommands = instanceManager.getCommandMap().getKnownCommands();

        instanceManager.getPlugins().remove(plugin);
        instanceManager.getLookupNames().remove(plugin.getName());
        instanceManager.getLookupNames().remove(plugin.getName().toLowerCase());

        HandlerList.unregisterAll(plugin);

        Set<String> keysToRemove = knownCommands.entrySet().stream()
                .filter(e -> e.getValue() instanceof PluginCommand pluginCommand && pluginCommand.getPlugin().equals(plugin))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        keysToRemove.forEach(knownCommands::remove);

        Bukkit.getScheduler().cancelTasks(plugin);
        Bukkit.getServicesManager().unregisterAll(plugin);
        closeClassLoader(plugin);
        syncCommands();

        System.gc();
    }

    protected void closeClassLoader(@NotNull Plugin plugin) {
        try {
            if (plugin.getClass().getClassLoader() instanceof URLClassLoader urlClassLoader) urlClassLoader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void stopPluginThreads(Plugin target) {
        Set<Thread> threads = ThreadUtils.getThreadsFromPlugin(target);
        if (threads.isEmpty()) return;

        plugin.getLogger().info("Interrupting " + threads.size() + " thread(s) for plugin: " + plugin.getName());

        for (Thread thread : threads) {
            try {
                plugin.getLogger().warning("Interrupting thread: " + thread.getName());

                thread.interrupt();
                thread.join(2000);

                if (thread.isAlive()) {
                    // Hopefully we are never getting here
                    plugin.getLogger().warning("Thread is still alive after interruption: " + thread.getName());
                } else {
                    plugin.getLogger().info("Thread stopped cleanly: " + thread.getName());
                }
            } catch (Throwable t) {
                plugin.getLogger().log(Level.SEVERE, "Error while stopping thread: " + thread.getName(), t);
            }
        }
    }

}
