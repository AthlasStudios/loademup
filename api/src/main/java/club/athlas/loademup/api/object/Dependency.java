package club.athlas.loademup.api.object;

import club.athlas.loademup.api.platform.Platform;
import org.bukkit.Bukkit;

public record Dependency(String name, boolean required, Platform platform) {

    public boolean isLoaded() {
        return Bukkit.getPluginManager().getPlugin(name) != null;
    }

}
