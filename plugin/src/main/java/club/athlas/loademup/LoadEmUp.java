package club.athlas.loademup;

import club.athlas.loademup.api.LoadEmUpAPI;
import club.athlas.loademup.api.constant.Environment;
import club.athlas.loademup.api.loader.PluginLoader;
import club.athlas.loademup.api.platform.PlatformManger;
import club.athlas.loademup.api.virustotal.VirusTotal;
import club.athlas.loademup.config.ConfigLoader;
import club.athlas.loademup.config.Messages;
import club.athlas.loademup.integration.ModrinthUpdateChecker;
import club.athlas.loademup.integration.VirusTotalClient;
import club.athlas.loademup.server.commands.LEUCommand;
import club.athlas.loademup.server.platform.BasePlatformManager;
import dev.gurwi.prism.paper.PaperPlatform;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public final class LoadEmUp extends JavaPlugin implements LoadEmUpAPI {

    private final Environment environment = Environment.DEVELOPMENT;

    private BukkitAudiences adventure;
    private ConfigLoader configLoader;
    private PlatformManger platformManger;
    private AtomicReference<VirusTotal> virusTotalIntegration;
    private ModrinthUpdateChecker updateChecker;

    @Override
    public void onLoad() {
        getLogger().info("Loading LoadEmUp...");
    }

    @Override
    public void onEnable() {
        this.adventure = BukkitAudiences.create(this);
        this.configLoader = new ConfigLoader(this).load();
        this.platformManger = new BasePlatformManager(this);
        this.virusTotalIntegration = VirusTotalClient.initVirusTotalClient(this);
        this.updateChecker = new ModrinthUpdateChecker(this, "loademup");

        if (platformManger.getIntegration() == null) {
            getLogger().severe("Unsupported Platform!");
            Bukkit.getPluginManager().disablePlugin(this);

            return;
        }

        getLogger().info("Initialized Plugin Loader for the " + platformManger.getIntegration().getPlatform().getIdentifier() + " platform.");

        PaperPlatform.configure(this)
                .init()
                .getCommandsRegistry()
                .register(new LEUCommand(this).build());

        Bukkit.getServicesManager().register(LoadEmUpAPI.class, this, this, ServicePriority.Normal);

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
    public PlatformManger getPlatformManger() {
        return platformManger;
    }

    @Override
    public @NotNull PluginLoader getLoader() {
        return platformManger.getIntegration().getLoader();
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

}
