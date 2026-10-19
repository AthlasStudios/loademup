package club.athlas.loademup.server.commands;

import club.athlas.loademup.LoadEmUp;
import club.athlas.loademup.api.constant.LEUPermission;
import club.athlas.loademup.server.commands.base.PluginCommand;
import club.athlas.loademup.server.commands.sub.*;
import dev.gurwi.prism.bukkit.command.executor.GenericCommandExecutor;
import dev.gurwi.prism.bukkit.permission.BukkitPermission;

public class LEUCommand extends PluginCommand<LoadEmUp> {

    public LEUCommand(LoadEmUp plugin) {
        super(plugin, "loademup", "leu");
    }

    @Override
    public void setup() {
        withPermission(BukkitPermission.startsWith(LEUPermission.COMMAND_PREFIX));

        HelpCommand helpCommand = new HelpCommand(plugin);
        helpCommand.setup();

        withSubCommands(
                new ReloadCommand(plugin),
                new EnableCommand(plugin),
                new DisableCommand(plugin),
                new LoadCommand(plugin),
                new UnloadCommand(plugin),
                new ScanCommand(plugin),
                new InfoCommand(plugin),
                helpCommand
        );

        executes(helpCommand.build().getExecutor(GenericCommandExecutor.class).orElseThrow());
    }
}