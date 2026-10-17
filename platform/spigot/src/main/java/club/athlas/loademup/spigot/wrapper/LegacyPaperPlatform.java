package club.athlas.loademup.spigot.wrapper;

import club.athlas.loademup.api.platform.DefPlatform;
import club.athlas.loademup.api.platform.Platform;
import club.athlas.loademup.spigot.SpigotPlatform;
import org.bukkit.plugin.java.JavaPlugin;

public class LegacyPaperPlatform extends SpigotPlatform {

    public LegacyPaperPlatform(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public Platform getPlatform() {
        return DefPlatform.LEGACY_PAPER;
    }

}
