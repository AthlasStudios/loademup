package club.athlas.loademup.common.wrapper;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public interface WrappedInstanceManager {

    List<Plugin> getPlugins();

    Map<String, Plugin> getLookupNames();

    PluginManager getPluginManager();

    WrappedCommandMap getCommandMap();

    default Server getServer() {
        return Bukkit.getServer();
    }

}
