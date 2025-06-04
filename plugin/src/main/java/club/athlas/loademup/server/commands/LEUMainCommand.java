package club.athlas.loademup.server.commands;

import club.athlas.loademup.LoadEmUp;
import me.gurwi.commandframework.arguments.StringArgument;
import me.gurwi.commandframework.usable.CommandAPI;
import me.gurwi.commandframework.utility.CommandSupplier;

import java.io.File;
import java.util.stream.Collectors;

public class LEUMainCommand extends CommandSupplier {

    private final LoadEmUp plugin;

    public LEUMainCommand(LoadEmUp plugin) {
        super("loademup");

        this.plugin = plugin;
    }

    @Override
    public CommandAPI get() {
        return create(getName())
                .withArguments(
                        new StringArgument("plugin")
                                .replaceSuggestions(sender ->
                                        plugin.getLoader().getPluginJarFiles()
                                                .stream()
                                                .map(File::getName)
                                                .collect(Collectors.toSet())
                                )
                )
                .executes((sender, args) -> {
                    String pluginName = (String) args.get("plugin");

                    plugin.getLoader().getPlugin(pluginName).ifPresent(pl -> plugin.getLoader().unload(pl));

//                    plugin.getLoader().getPluginFile(pluginName).ifPresent(file -> {
//                        try {
//                            Set<Dependency> missingDepends = plugin.getLoader().getMissingDependencies(file);
//
//                            if (!missingDepends.isEmpty()) {
//                                sender.sendMessage("Missing Dependencies for " + pluginName);
//                                missingDepends.forEach(dependency -> sender.sendMessage(dependency.name()));
//                                return;
//                            }
//
//                            plugin.getLoader().load(file);
//                        } catch (InvalidPluginException | InvalidDescriptionException e) {
//                            throw new RuntimeException(e);
//                        }
//                    });
                });
    }

}
