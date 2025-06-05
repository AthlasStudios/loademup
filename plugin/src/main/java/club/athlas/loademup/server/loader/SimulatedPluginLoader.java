package club.athlas.loademup.server.loader;

import club.athlas.loademup.api.loader.PluginLoader;
import club.athlas.loademup.api.loader.Simulator;
import club.athlas.loademup.api.object.SimulationResult;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SimulatedPluginLoader implements Simulator {

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final PluginLoader pluginLoader;

    public SimulatedPluginLoader(PluginLoader pluginLoader) {
        this.pluginLoader = pluginLoader;
    }

    @Override
    public SimulationResult reload(Plugin plugin) {
        return new SimulationResult();
    }

    @Override
    public SimulationResult load(File file) {
        CompletableFuture.supplyAsync(() -> {
//
//            try {
//                pluginLoader.load(new File())
//                        .ifPresent(plugin -> {
//                            plugin.onLoad();
//                            plugin.onEnable();
//                        });
//
//            } catch (InvalidPluginException | InvalidDescriptionException e) {
//                throw new RuntimeException(e);
//            }

            return null;
        }, executor).whenComplete((result, throwable) -> {
            if (throwable != null) throwable.printStackTrace();
        });

        return new SimulationResult();
    }

    @Override
    public SimulationResult unload(Plugin plugin) {
        return new SimulationResult();
    }

}
