package club.athlas.loademup.server.loader;

import club.athlas.loademup.api.loader.PluginLoader;
import club.athlas.loademup.api.loader.PluginLoadersManager;
import club.athlas.loademup.api.platform.DefPlatform;
import club.athlas.loademup.api.platform.Platform;

import java.util.HashMap;
import java.util.Map;

public class LoadersManager implements PluginLoadersManager {

    private final Map<Platform, PluginLoader> loaders = new HashMap<>();

    @Override
    public void registerPluginLoader(Platform platform, PluginLoader loader) {
        loaders.put(platform, loader);
    }

    @Override
    public Map<Platform, PluginLoader> getLoaders() {
        return loaders;
    }

    @Override
    public PluginLoader findBestLoader() {
        return loaders.get(DefPlatform.getCurrentPlatform());
    }

}
