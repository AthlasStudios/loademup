package club.athlas.loademup.api;

import club.athlas.loademup.api.loader.PluginLoader;
import club.athlas.loademup.api.loader.PluginLoadersManager;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface LoadEmUpAPI {

    PluginLoadersManager getLoadersManager();

    @NotNull PluginLoader getLoader();

}
