package club.athlas.loademup;

import club.athlas.loademup.api.LoadEmUpAPI;
import club.athlas.loademup.api.constant.Environment;
import club.athlas.loademup.api.loader.PluginLoader;
import club.athlas.loademup.api.loader.PluginLoadersManager;
import club.athlas.loademup.api.platform.DefPlatform;
import club.athlas.loademup.config.ConfigLoader;
import club.athlas.loademup.config.Messages;
import club.athlas.loademup.integration.ModrinthUpdateChecker;
import club.athlas.loademup.server.commands.LEUMainCommand;
import club.athlas.loademup.server.loader.LoadersManager;
import club.athlas.loademup.server.loader.PaperPluginLoader;
import club.athlas.loademup.server.loader.SpigotPluginLoader;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class LoadEmUp extends JavaPlugin implements LoadEmUpAPI {

    private final PluginLoadersManager loadersManager = new LoadersManager();

    private Environment environment = Environment.PRODUCTION;

    private PluginLoader pluginLoader;
    private BukkitAudiences adventure;
    private ConfigLoader configLoader;
    private ModrinthUpdateChecker updateChecker;

    @Override
    public void onLoad() {
        loadersManager.registerPluginLoader(DefPlatform.SPIGOT, new SpigotPluginLoader(this));
        loadersManager.registerPluginLoader(DefPlatform.PAPER, new PaperPluginLoader(this));
        loadersManager.registerPluginLoader(DefPlatform.PURPUR, new PaperPluginLoader(this));
    }

    @Override
    public void onEnable() {
        this.pluginLoader = loadersManager.findBestLoader();
        this.adventure = BukkitAudiences.create(this);
        this.configLoader = new ConfigLoader(this);
        this.updateChecker = new ModrinthUpdateChecker(this, "inventorytracker"); // TODO -> CHANGE

        if (pluginLoader == null) {
            getLogger().severe("No plugin loader found!");
            Bukkit.getPluginManager().disablePlugin(this);

            return;
        }

        getLogger().info("Initialized Plugin Loader for the " + DefPlatform.getCurrentPlatform().getIdentifier() + " platform.");

        configLoader.load();

        new LEUMainCommand(this).get().register(this);

        updateChecker.cacheLatestVersion();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void reload() {
        configLoader.reload();
        updateChecker.cacheLatestVersion();
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

    public Messages getMessages() {
        return configLoader.getMessages();
    }

    public @NotNull BukkitAudiences getAdventure() {
        return adventure;
    }

    public ModrinthUpdateChecker getUpdateChecker() {
        return updateChecker;
    }

}
