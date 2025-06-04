package club.athlas.loademup.server.loader;

import club.athlas.loademup.LoadEmUp;
import club.athlas.loademup.api.object.Dependency;
import club.athlas.loademup.api.platform.DefPlatform;
import club.athlas.loademup.server.wrapper.SpigotPluginInstanceManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("removal")
public class SpigotPluginLoader extends BasePluginLoader {

    private final SpigotPluginInstanceManager instanceManager;

    public SpigotPluginLoader(LoadEmUp plugin) {
        super(plugin);

        try {
            this.instanceManager = new SpigotPluginInstanceManager((SimplePluginManager) Bukkit.getPluginManager());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unload(Plugin plugin) {
        Map<String, Command> knownCommands = instanceManager.getCommandMap().getKnownCommands();

        disablePlugin(plugin);

        instanceManager.getPlugins().remove(plugin);
        instanceManager.getLookupNames().remove(plugin.getName());
        instanceManager.getLookupNames().remove(plugin.getName().toLowerCase());

        HandlerList.unregisterAll(plugin);

        Set<String> keysToRemove = knownCommands.entrySet().stream()
                .filter(e -> e.getValue() instanceof PluginCommand pluginCommand && pluginCommand.getPlugin().equals(plugin))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        keysToRemove.forEach(knownCommands::remove);

        closeClassLoader(plugin);
        syncCommands();

        System.gc();
    }

    @Override
    public Set<Dependency> getDependencies(@NotNull PluginDescriptionFile description) {
        Set<Dependency> dependencies = new HashSet<>();

        description.getDepend()
                .forEach(dependency -> dependencies.add(new Dependency(dependency, true, DefPlatform.SPIGOT)));

        description.getSoftDepend()
                .forEach(dependency -> dependencies.add(new Dependency(dependency, false, DefPlatform.SPIGOT)));

        return dependencies;
    }

}
