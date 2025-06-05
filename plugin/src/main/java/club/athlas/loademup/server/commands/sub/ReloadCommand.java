package club.athlas.loademup.server.commands.sub;

import club.athlas.loademup.LoadEmUp;
import club.athlas.loademup.api.exceptions.PluginFileNotFoundException;
import club.athlas.loademup.api.loader.PluginLoader;
import me.gurwi.commandframework.arguments.BooleanArgument;
import me.gurwi.commandframework.arguments.StringArgument;
import me.gurwi.commandframework.usable.SubCommand;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;

import java.util.stream.Collectors;

public class ReloadCommand extends SubCommand<LoadEmUp> {

    private final PluginLoader loader;

    public ReloadCommand(LoadEmUp plugin) {
        super(plugin, "reload");

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

        withOptionalArguments(
                new BooleanArgument("reloadDependants")
        );

        executes((sender, args) -> {
            String pluginName = (String) args.get("plugin");
            boolean reloadDependants = args.getOptional("reloadDependants")
                    .map(o -> (boolean) o)
                    .orElse(false);

            sender.sendMessage(String.valueOf(reloadDependants));

            loader.getPlugin(pluginName).ifPresentOrElse(
                    pl -> {
                        try {
                            loader.reload(pl);
                            sender.sendMessage("Plugin " + pluginName + " reloaded.");

                            if (reloadDependants) {
                                loader.getDependents(pl).forEach(dependency -> {
                                    if (!dependency.isLoaded()) return;

                                    dependency.getPlugin().ifPresent(dependant -> {
                                        try {
                                            loader.reload(dependant);
                                            sender.sendMessage("Reloaded dependant " + dependant.getName());
                                        } catch (InvalidPluginException | InvalidDescriptionException e) {
                                            throw new RuntimeException(e);
                                        }
                                    });
                                });
                            }

                        } catch (InvalidPluginException | InvalidDescriptionException | PluginFileNotFoundException e) {
                            sender.sendMessage("Plugin " + pluginName + " could not be reloaded."); // TODO
                            plugin.getLogger().severe("Plugin " + pluginName + " could not be reloaded.");
                            e.printStackTrace();
                        }
                    },
                    () -> sender.sendMessage("Not found plugin: " + pluginName) // TODO
            );
        });
    }

}
