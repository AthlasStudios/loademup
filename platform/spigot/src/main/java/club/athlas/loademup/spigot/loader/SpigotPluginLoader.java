package club.athlas.loademup.spigot.loader;

import club.athlas.loademup.api.object.Dependency;
import club.athlas.loademup.api.platform.DefPlatform;
import club.athlas.loademup.common.loader.BasePluginLoader;
import club.athlas.loademup.spigot.wrapper.SpigotPluginInstanceManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("removal")
public class SpigotPluginLoader extends BasePluginLoader {

    private final SpigotPluginInstanceManager instanceManager;

    public SpigotPluginLoader(JavaPlugin plugin) {
        super(plugin);

        try {
            this.instanceManager = new SpigotPluginInstanceManager((SimplePluginManager) Bukkit.getPluginManager());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public SpigotPluginLoader(JavaPlugin plugin, SimplePluginManager pluginManager) {
        super(plugin, pluginManager);

        try {
            this.instanceManager = new SpigotPluginInstanceManager(pluginManager);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unload(Plugin plugin) {
        disablePlugin(plugin);
        cleanup(instanceManager, plugin);
    }

    @Override
    public List<Dependency> getDependents(@NotNull Plugin plugin) {
        List<Dependency> dependentPlugins = new ArrayList<>();
        String targetName = plugin.getName();

        for (Plugin checked : Bukkit.getPluginManager().getPlugins()) {
            getDependencies(checked).stream()
                    .filter(dependency -> dependency.name().equals(targetName))
                    .findFirst()
                    .ifPresent(dependency -> dependentPlugins.add(new Dependency(checked.getName(), dependency.required(), DefPlatform.SPIGOT)));
        }

        return dependentPlugins;
    }

    @Override
    public List<Dependency> getDependencies(@NotNull PluginDescriptionFile description) {
        List<Dependency> dependencies = new ArrayList<>();

        description.getDepend()
                .forEach(dependency -> dependencies.add(new Dependency(dependency, true, DefPlatform.SPIGOT)));

        description.getSoftDepend()
                .forEach(dependency -> dependencies.add(new Dependency(dependency, false, DefPlatform.SPIGOT)));

        return dependencies;
    }

    @Override
    public List<Dependency> getDependencies(@NotNull Plugin plugin) {
        return getDependencies(plugin.getDescription());
    }

}
