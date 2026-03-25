package club.athlas.loademup.api;

import club.athlas.loademup.api.constant.Environment;
import club.athlas.loademup.api.loader.PluginLoader;
import club.athlas.loademup.api.loader.PluginLoadersManager;
import club.athlas.loademup.api.virustotal.VirusTotal;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("unused")
public interface LoadEmUpAPI {

    PluginLoadersManager getLoadersManager();

    @NotNull PluginLoader getLoader();

    Environment getEnvironment();

    Optional<VirusTotal> getVirusTotalIntegration();

    static @Nullable LoadEmUpAPI getAPI() {
        if (Bukkit.getPluginManager().getPlugin("LoadEmUp") == null) return null;
        RegisteredServiceProvider<LoadEmUpAPI> serviceProvider = Bukkit.getServicesManager().getRegistration(LoadEmUpAPI.class);
        if (serviceProvider == null) return null;

        return serviceProvider.getProvider();
    }

}
