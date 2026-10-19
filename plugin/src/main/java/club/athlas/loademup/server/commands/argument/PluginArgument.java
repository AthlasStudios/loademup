package club.athlas.loademup.server.commands.argument;

import club.athlas.loademup.LoadEmUp;
import club.athlas.loademup.api.loader.PluginLoader;
import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.exception.ArgumentParseException;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class PluginArgument extends BukkitArgument<PluginArgument, Plugin> {

    private final LoadEmUp plugin;
    private final PluginLoader loader;

    public PluginArgument(@NotNull LoadEmUp plugin, String name) {
        super(name);

        this.plugin = plugin;
        this.loader = plugin.getLoader();

        replaceSuggestions(
                loader.getPlugins().stream()
                        .map(Plugin::getName)
                        .toList()
        );
    }

    @Override
    public Plugin parse(@NotNull CommandSender sender, @NotNull String input) throws ArgumentParseException {
        return loader.getPlugin(input)
                .orElseThrow(() -> new ArgumentParseException(this, input, "Plugin not found."));
    }

}
