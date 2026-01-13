package club.athlas.loademup.server.commands;

import club.athlas.loademup.LoadEmUp;
import club.athlas.loademup.server.commands.base.PluginCommand;
import club.athlas.loademup.server.commands.sub.*;

public class LEUMainCommand extends PluginCommand<LoadEmUp> {

    public LEUMainCommand(LoadEmUp plugin) {
        super(plugin, "loademup");
    }

    @Override
    public void setup() {
        HelpCommand helpCommand = new HelpCommand(plugin);
        helpCommand.setup();

        withAliases("leu");

        withRequirement(sender -> sender.getEffectivePermissions().stream()
                .anyMatch(p -> p.getPermission().startsWith("loademup.")) ||
                sender.isOp() || sender.hasPermission("*")
        );

        withSubcommands(
                new ReloadCommand(plugin),
                new EnableCommand(plugin),
                new DisableCommand(plugin),
                new LoadCommand(plugin),
                new UnloadCommand(plugin),
                new ScanCommand(plugin),
                new InfoCommand(plugin),
                new HelpCommand(plugin)
        );

        setExecutor(helpCommand.getExecutor());
    }

}
