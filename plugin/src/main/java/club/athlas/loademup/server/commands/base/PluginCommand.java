package club.athlas.loademup.server.commands.base;

import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.command.PrismBukkitCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class PluginCommand<Plugin extends JavaPlugin> extends PrismBukkitCommand {

    protected final Plugin plugin;

    private boolean configured = false;

    public PluginCommand(Plugin plugin, String name, List<String> aliases) {
        super(name, aliases);
        this.plugin = plugin;
    }

    public PluginCommand(Plugin plugin, String name, String... aliases) {
        super(name, aliases);
        this.plugin = plugin;
    }

    protected abstract void setup();

    private void configure() {
        setup();
        configured = true;
    }

    @Override
    public BukkitCommandDefinition build(@Nullable BukkitCommandDefinition parent) {
        configure();
        return super.build(parent);
    }

}
