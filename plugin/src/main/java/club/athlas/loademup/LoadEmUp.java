package club.athlas.loademup;

import club.athlas.loademup.api.LoadEmUpAPI;
import club.athlas.loademup.api.constant.Environment;
import club.athlas.loademup.api.loader.PluginLoader;
import club.athlas.loademup.api.loader.PluginLoadersManager;
import club.athlas.loademup.api.platform.DefPlatform;
import club.athlas.loademup.api.virustotal.VirusTotal;
import club.athlas.loademup.config.ConfigLoader;
import club.athlas.loademup.config.Messages;
import club.athlas.loademup.config.constant.ConfigKey;
import club.athlas.loademup.integration.ModrinthUpdateChecker;
import club.athlas.loademup.integration.VirusTotalClient;
import club.athlas.loademup.server.commands.LEUMainCommand;
import club.athlas.loademup.server.loader.LoadersManager;
import club.athlas.loademup.server.loader.PaperPluginLoader;
import club.athlas.loademup.server.loader.SpigotPluginLoader;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

public final class LoadEmUp extends JavaPlugin implements LoadEmUpAPI {

    private final PluginLoadersManager loadersManager = new LoadersManager();
    private final Environment environment = Environment.PRODUCTION;

    private PluginLoader pluginLoader;
    private BukkitAudiences adventure;
    private ConfigLoader configLoader;
    private AtomicReference<VirusTotal> virusTotalIntegration;
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
        this.configLoader = new ConfigLoader(this).load();
        this.virusTotalIntegration = initVirusTotalClient();
        this.updateChecker = new ModrinthUpdateChecker(this, "inventorytracker"); // TODO -> CHANGE

        if (pluginLoader == null) {
            getLogger().severe("No plugin loader found!");
            Bukkit.getPluginManager().disablePlugin(this);

            return;
        }

        getLogger().info("Initialized Plugin Loader for the " + DefPlatform.getCurrentPlatform().getIdentifier() + " platform.");

        new LEUMainCommand(this).get().register(this);

        updateChecker.cacheLatestVersion();
    }

    @Override
    public void onDisable() {
        getVirusTotalIntegration().ifPresent(VirusTotal::shutdown);
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
    public @NotNull Optional<VirusTotal> getVirusTotalIntegration() {
        return Optional.ofNullable(virusTotalIntegration.get());
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

    private @NotNull AtomicReference<VirusTotal> initVirusTotalClient() {
        AtomicReference<VirusTotal> clientRef = new AtomicReference<>(null);

        if (!configLoader.getBool(ConfigKey.VIRUS_SCANNER_ENABLED, false)) return clientRef;

        String apiKey = configLoader.getString(ConfigKey.VIRUS_SCANNER_API_KEY, "");

        if (apiKey.isEmpty()) return clientRef;

        VirusTotalClient virusTotalClient = new VirusTotalClient(apiKey, getLogger());

        virusTotalClient.hasValidApiKey().whenComplete(((result, throwable) -> {
            if (throwable != null) {
                getLogger().log(Level.WARNING, "Unable to initialize VirusTotalClient.", throwable);
                virusTotalClient.shutdown();
                return;
            }

            if (!result) {
                getLogger().log(Level.WARNING, "The current VirusTotal ApiKey is invalid! Removing VT...");
                virusTotalClient.shutdown();
                return;
            }

            getLogger().log(Level.INFO, "Initializing VirusTotalClient...");
            clientRef.set(virusTotalClient);
        }));

        return clientRef;
    }

}
