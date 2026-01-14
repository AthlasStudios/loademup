package club.athlas.loademup.server.commands.base;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class PluginCommand<T extends JavaPlugin> extends Command {

    protected final T plugin;

    public PluginCommand(T plugin, String commandName) {
        super(commandName);

        this.plugin = plugin;
    }

}