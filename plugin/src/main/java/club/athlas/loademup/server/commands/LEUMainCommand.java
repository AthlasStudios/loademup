package club.athlas.loademup.server.commands;

import club.athlas.loademup.LoadEmUp;
import club.athlas.loademup.server.commands.sub.*;
import me.gurwi.commandframework.usable.CommandAPI;
import me.gurwi.commandframework.utility.CommandSupplier;

public class LEUMainCommand extends CommandSupplier {

    private final LoadEmUp plugin;

    public LEUMainCommand(LoadEmUp plugin) {
        super("loademup");

        this.plugin = plugin;
    }

    @Override
    public CommandAPI get() {
        return create(getName())
                .executes((sender, args) -> {
                })
                .withSubCommands(
                        new ReloadCommand(plugin),
                        new EnableCommand(plugin),
                        new DisableCommand(plugin),
                        new SimulateLoadCommand(plugin),
                        new InfoCommand(plugin)
                );
    }

}
