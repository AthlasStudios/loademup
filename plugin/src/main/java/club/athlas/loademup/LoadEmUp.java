package club.athlas.loademup;

import club.athlas.loademup.api.LoadEmUpAPI;
import club.athlas.loademup.api.constant.Environment;
import club.athlas.loademup.api.loader.PluginLoader;
import club.athlas.loademup.api.loader.PluginLoadersManager;
import club.athlas.loademup.api.platform.DefPlatform;
import club.athlas.loademup.config.ConfigLoader;
import club.athlas.loademup.server.commands.LEUMainCommand;
import club.athlas.loademup.server.loader.LoadersManager;
import club.athlas.loademup.server.loader.PaperPluginLoader;
import club.athlas.loademup.server.loader.SpigotPluginLoader;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class LoadEmUp extends JavaPlugin implements LoadEmUpAPI {

    private final ConfigLoader configLoader = new ConfigLoader(this);
    private final PluginLoadersManager loadersManager = new LoadersManager();

    private Environment environment = Environment.PRODUCTION;

    private BukkitAudiences adventure;
    private PluginLoader pluginLoader;

    @Override
    public void onLoad() {
        loadersManager.registerPluginLoader(DefPlatform.SPIGOT, new SpigotPluginLoader(this));
        loadersManager.registerPluginLoader(DefPlatform.PAPER, new PaperPluginLoader(this));
        loadersManager.registerPluginLoader(DefPlatform.PURPUR, new PaperPluginLoader(this));
    }

    @Override
    public void onEnable() {
        configLoader.load();

        this.adventure = BukkitAudiences.create(this);
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

    @Override
    public Environment getEnvironment() {
        return environment;
    }

    public ConfigLoader getConfigLoader() {
        return configLoader;
    }

    public @NotNull BukkitAudiences getAdventure() {
        return adventure;
    }

}
