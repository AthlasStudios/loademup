package club.athlas.loademup;

import club.athlas.loademup.api.LoadEmUpAPI;
import club.athlas.loademup.api.loader.PluginLoader;
import club.athlas.loademup.api.loader.PluginLoadersManager;
import club.athlas.loademup.api.platform.DefPlatform;
import club.athlas.loademup.server.commands.LEUMainCommand;
import club.athlas.loademup.server.loader.LoadersManager;
import club.athlas.loademup.server.loader.PaperPluginLoader;
import club.athlas.loademup.server.loader.SpigotPluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class LoadEmUp extends JavaPlugin implements LoadEmUpAPI {

    private final PluginLoadersManager loadersManager;

    private PluginLoader pluginLoader;

    public LoadEmUp() {
        this.loadersManager = new LoadersManager(this);
    }

    @Override
    public void onLoad() {
        loadersManager.registerPluginLoader(DefPlatform.SPIGOT, new SpigotPluginLoader(this));
        loadersManager.registerPluginLoader(DefPlatform.PAPER, new PaperPluginLoader(this));
        loadersManager.registerPluginLoader(DefPlatform.PURPUR, new PaperPluginLoader(this));
    }

    @Override
    public void onEnable() {
        this.pluginLoader = loadersManager.findBestLoader();

        getLogger().info("Initialized Plugin Loader for the " + DefPlatform.getCurrentPlatform().getIdentifier() + " platform.");

        new LEUMainCommand(this).get().register(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public PluginLoadersManager getLoadersManager() {
        return loadersManager;
    }

    @Override
    public @NotNull PluginLoader getLoader() {
        return pluginLoader;
    }

}
