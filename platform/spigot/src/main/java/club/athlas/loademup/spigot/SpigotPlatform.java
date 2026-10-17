package club.athlas.loademup.spigot;

import club.athlas.loademup.api.loader.PluginLoader;
import club.athlas.loademup.api.platform.DefPlatform;
import club.athlas.loademup.api.platform.Platform;
import club.athlas.loademup.api.platform.PlatformIntegration;
import club.athlas.loademup.spigot.loader.SpigotPluginLoader;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotPlatform extends PlatformIntegration {

    public SpigotPlatform(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public PluginLoader getLoader() {
        return new SpigotPluginLoader(plugin);
    }

    @Override
    public Platform getPlatform() {
        return DefPlatform.SPIGOT;
    }

}
