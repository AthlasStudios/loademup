package club.athlas.loademup.server.loader;

import club.athlas.loademup.LoadEmUp;
import club.athlas.loademup.api.object.Dependency;
import club.athlas.loademup.api.platform.DefPlatform;
import club.athlas.loademup.server.wrapper.PaperPluginInstanceManager;
import io.papermc.paper.plugin.configuration.PluginMeta;
import io.papermc.paper.plugin.entrypoint.Entrypoint;
import io.papermc.paper.plugin.entrypoint.LaunchEntryPointHandler;
import io.papermc.paper.plugin.manager.PaperPluginManagerImpl;
import io.papermc.paper.plugin.provider.PluginProvider;
import io.papermc.paper.plugin.storage.ProviderStorage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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

        disablePlugin(plugin);

        storage.values().forEach(providerStorage -> {
            Iterator<? extends PluginProvider<?>> providerIterator = providerStorage.getRegisteredProviders().iterator();

            while (providerIterator.hasNext()) {
                PluginProvider<?> provider = providerIterator.next();

                if (!provider.getMeta().getName().equals(plugin.getName())) continue;
                providerIterator.remove();
            }
        });

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
                    .ifPresent(dependency -> {
                        dependentPlugins.add(new Dependency(checked.getName(), dependency.required(), DefPlatform.PAPER));
                    });
        }

        return dependentPlugins;
    }

    @Override
    public List<Dependency> getDependencies(@NotNull PluginDescriptionFile description) {
        return getPluginDependencies(description);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public List<Dependency> getDependencies(@NotNull Plugin plugin) {
        return getPluginDependencies(plugin.getPluginMeta());
    }

    @SuppressWarnings("UnstableApiUsage")
    private @NotNull List<Dependency> getPluginDependencies(@NotNull PluginMeta meta) {
        List<Dependency> dependencies = new ArrayList<>();

        meta.getPluginDependencies()
                .forEach(dependency -> dependencies.add(new Dependency(dependency, true, DefPlatform.PAPER)));

        meta.getPluginSoftDependencies()
                .forEach(dependency -> dependencies.add(new Dependency(dependency, false, DefPlatform.PAPER)));

        return dependencies;
    }

}
