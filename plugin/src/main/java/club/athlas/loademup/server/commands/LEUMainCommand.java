package club.athlas.loademup.server.commands;

import club.athlas.loademup.LoadEmUp;
import club.athlas.loademup.server.commands.sub.*;
import me.gurwi.commandframework.executors.ExecutorType;
import me.gurwi.commandframework.executors.GenericCommandExecutor;
import me.gurwi.commandframework.permissions.StartsWithPermission;
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
                .withPermissions(new StartsWithPermission("loademup."))
                .withSubCommands(
                        new ReloadCommand(plugin),
                        new EnableCommand(plugin),
                        new DisableCommand(plugin),
                        new LoadCommand(plugin),
                        new UnloadCommand(plugin),
                        new ScanCommand(plugin),
                        new InfoCommand(plugin),
                        new HelpCommand(plugin)
                ).executes((GenericCommandExecutor) new HelpCommand(plugin).getMeta().getExecutors().get(ExecutorType.GENERIC));
    }

}
