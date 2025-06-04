package club.athlas.loademup.api.exceptions;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class PluginAlreadyLoadedException extends IllegalStateException {

    public PluginAlreadyLoadedException(@NotNull File file) {
        super("Plugin is already loaded: " + file.getName());
    }

}
