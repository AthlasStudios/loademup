package club.athlas.loademup.server.commands.base;

import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.command.PrismBukkitCommand;
import dev.gurwi.prism.platform.CommandsRegistry;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class PluginCommand<Plugin extends JavaPlugin> extends PrismBukkitCommand {

    protected final Plugin plugin;

    public PluginCommand(Plugin plugin, String name, List<String> aliases) {
        super(name, aliases);
        this.plugin = plugin;
    }

    public PluginCommand(Plugin plugin, String name, String... aliases) {
        super(name, aliases);
        this.plugin = plugin;
    }

    public abstract void setup();

    @Override
    public BukkitCommandDefinition register(@NotNull CommandsRegistry<?, BukkitCommandDefinition> registry) {
        setup();
        return super.register(registry);
    }

    @Override
    public PrismBukkitCommand withSubCommands(PrismBukkitCommand @NotNull ... children) {
        for (PrismBukkitCommand child : children) {
            if (child instanceof PluginCommand<?> command) command.setup();
        }

        return super.withSubCommands(children);
    }
}
