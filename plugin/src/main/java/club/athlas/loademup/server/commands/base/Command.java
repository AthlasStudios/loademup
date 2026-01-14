package club.athlas.loademup.server.commands.base;

import dev.jorel.commandapi.CommandAPICommand;
import org.jetbrains.annotations.NotNull;

public abstract class Command extends CommandAPICommand {

    public Command(String commandName) {
        super(commandName);
    }

    public abstract void setup();

    @Override
    public final CommandAPICommand withSubcommands(CommandAPICommand @NotNull ... subcommands) {
        for (CommandAPICommand subcommand : subcommands) {
            if (subcommand instanceof Command) {
                ((Command) subcommand).setup();
            }
        }

        return super.withSubcommands(subcommands);
    }

    @Override
    public final void register() {
        setup();

        super.register();
    }

}
