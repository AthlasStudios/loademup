package club.athlas.loademup.paper;

import club.athlas.loademup.api.platform.DefPlatform;
import club.athlas.loademup.api.platform.PlatformIntegration;
import club.athlas.loademup.api.loader.PluginLoader;
import club.athlas.loademup.api.platform.Platform;
import club.athlas.loademup.paper.loader.PaperPluginLoader;
import org.bukkit.plugin.java.JavaPlugin;

public class PaperPlatform extends PlatformIntegration {

    public PaperPlatform(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public PluginLoader getLoader() {
        return new PaperPluginLoader(plugin);
    }

    @Override
    public Platform getPlatform() {
        return DefPlatform.PAPER;
    }

}