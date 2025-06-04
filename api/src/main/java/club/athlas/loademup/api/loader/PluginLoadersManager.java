package club.athlas.loademup.api.loader;

import club.athlas.loademup.api.platform.Platform;

import java.util.Map;

@SuppressWarnings("unused")
public interface PluginLoadersManager {

    void registerPluginLoader(Platform platform, PluginLoader loader);

    Map<Platform, PluginLoader> getLoaders();

    PluginLoader findBestLoader();

}
