package club.athlas.loademup.server.wrapper;

import club.athlas.loademup.server.utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;

import java.util.Map;

public class WrappedCommandMap {

    private final Map<String, Command> knownCommands;
    private final Server server;

    public WrappedCommandMap(CommandMap commandMap) throws NoSuchFieldException, IllegalAccessException {
        knownCommands = ReflectionUtils.accessFieldFromType(
                SimpleCommandMap.class,
                commandMap,
                "knownCommands"
        );

        server = Bukkit.getServer();
    }

    public Map<String, Command> getKnownCommands() {
        return knownCommands;
    }

    public Server getServer() {
        return server;
    }

}
