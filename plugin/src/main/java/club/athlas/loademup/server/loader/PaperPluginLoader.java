package club.athlas.loademup.server.loader;

import club.athlas.loademup.LoadEmUp;
import club.athlas.loademup.api.object.Dependency;
import club.athlas.loademup.api.platform.DefPlatform;
import club.athlas.loademup.server.wrapper.PaperPluginInstanceManager;
import io.papermc.paper.plugin.entrypoint.Entrypoint;
import io.papermc.paper.plugin.entrypoint.LaunchEntryPointHandler;
import io.papermc.paper.plugin.manager.PaperPluginManagerImpl;
import io.papermc.paper.plugin.provider.PluginProvider;
import io.papermc.paper.plugin.storage.ProviderStorage;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PaperPluginLoader extends BasePluginLoader {

    private final PaperPluginInstanceManager instanceManager;

    public PaperPluginLoader(LoadEmUp plugin) {
        super(plugin, PaperPluginManagerImpl.getInstance());

        try {
            this.instanceManager = new PaperPluginInstanceManager(PaperPluginManagerImpl.getInstance());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void unload(Plugin plugin) {
        Map<Entrypoint<?>, ProviderStorage<?>> storage = LaunchEntryPointHandler.INSTANCE.getStorage();
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

        storage.values().forEach(providerStorage -> {
            Iterator<? extends PluginProvider<?>> providerIterator = providerStorage.getRegisteredProviders().iterator();

            while (providerIterator.hasNext()) {
                PluginProvider<?> provider = providerIterator.next();

                if (!provider.getMeta().getName().equals(plugin.getName())) continue;
                providerIterator.remove();
            }
        });

        closeClassLoader(plugin);
        syncCommands();

        System.gc();
    }

    @Override
    public Set<Dependency> getDependencies(@NotNull PluginDescriptionFile description) {
        Set<Dependency> dependencies = new HashSet<>();

        description.getPluginDependencies()
                .forEach(dependency -> dependencies.add(new Dependency(dependency, true, DefPlatform.PAPER)));

        description.getPluginSoftDependencies()
                .forEach(dependency -> dependencies.add(new Dependency(dependency, false, DefPlatform.PAPER)));

        return dependencies;
    }

}
