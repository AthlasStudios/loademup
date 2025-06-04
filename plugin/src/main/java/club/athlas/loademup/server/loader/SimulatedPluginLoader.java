package club.athlas.loademup.server.loader;

import club.athlas.loademup.api.loader.PluginLoader;
import club.athlas.loademup.api.loader.Simulator;
import club.athlas.loademup.api.object.SimulationResult;

public class SimulatedPluginLoader implements Simulator {

    private final PluginLoader pluginLoader;

    public SimulatedPluginLoader(PluginLoader pluginLoader) {
        this.pluginLoader = pluginLoader;
    }

    public SimulationResult reload() {
        return null;
    }

    public SimulationResult load() {
        return null;
    }

    public SimulationResult unload() {
        return null;
    }

}
