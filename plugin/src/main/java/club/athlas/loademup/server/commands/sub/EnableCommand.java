package club.athlas.loademup.server.commands.sub;

import club.athlas.loademup.LoadEmUp;
import club.athlas.loademup.api.loader.PluginLoader;
import me.gurwi.commandframework.arguments.StringArgument;
import me.gurwi.commandframework.usable.SubCommand;
import org.bukkit.plugin.Plugin;

import java.util.stream.Collectors;

public class EnableCommand extends SubCommand<LoadEmUp> {

    private final PluginLoader loader;

    public EnableCommand(LoadEmUp plugin) {
        super(plugin, "enable");

        this.loader = plugin.getLoader();
    }

    @Override
    public void setup() {
        withArguments(
                new StringArgument("plugin")
                        .replaceSuggestions(commandSender -> loader.getPlugins().stream()
                                .map(Plugin::getName)
                                .collect(Collectors.toList())
                        )
        );

        executes((sender, args) -> {
            String pluginName = (String) args.get("plugin");

            loader.getPlugin(pluginName).ifPresentOrElse(
                    pl -> {
                        loader.enablePlugin(pl);
                        sender.sendMessage("Enabled plugin " + pluginName);
                    },
                    () -> sender.sendMessage("Not found plugin: " + pluginName) // TODO
            );
        });
    }

}
