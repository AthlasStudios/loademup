package club.athlas.loademup.api.object;

import club.athlas.loademup.api.platform.Platform;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record Dependency(String name, boolean required, Platform platform) {

    public boolean isLoaded() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
        return plugin != null && plugin.isEnabled();
    }

    public @NotNull Optional<Plugin> getPlugin() {
        return Optional.ofNullable(Bukkit.getPluginManager().getPlugin(name));
    }

}
