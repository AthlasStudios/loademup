package club.athlas.loademup.api.platform;

import club.athlas.loademup.api.loader.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class PlatformIntegration {

    protected final JavaPlugin plugin;

    protected PlatformIntegration(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract PluginLoader getLoader();

    public abstract Platform getPlatform();

}
