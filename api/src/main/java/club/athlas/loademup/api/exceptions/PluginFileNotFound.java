package club.athlas.loademup.api.exceptions;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class PluginFileNotFound extends IllegalStateException {

    public PluginFileNotFound(@NotNull Plugin plugin) {
        super("Plugin file could not be found: " + plugin.getName());
    }

}
