package club.athlas.loademup.spigot.wrapper;

import club.athlas.loademup.common.utils.ReflectionUtils;
import club.athlas.loademup.common.wrapper.WrappedCommandMap;
import club.athlas.loademup.common.wrapper.WrappedInstanceManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;

import java.util.List;
import java.util.Map;

@SuppressWarnings("ALL")
public class SpigotPluginInstanceManager implements WrappedInstanceManager {

    private final List<Plugin> plugins;
    private final Map<String, Plugin> lookupNames;

    private final PluginManager pluginManager;
    private final WrappedCommandMap commandMap;

    public SpigotPluginInstanceManager(SimplePluginManager pluginManager) throws NoSuchFieldException, IllegalAccessException {
        this.pluginManager = pluginManager;

        this.plugins = ReflectionUtils.accessField(
                pluginManager,
                "plugins"
        );

        this.lookupNames = ReflectionUtils.accessField(
                pluginManager,
                "lookupNames"
        );

        this.commandMap = new WrappedCommandMap(
                ReflectionUtils.accessField(
                        pluginManager,
                        "commandMap"
                )
        );
    }

    @Override
    public List<Plugin> getPlugins() {
        return plugins;
    }

    @Override
    public Map<String, Plugin> getLookupNames() {
        return lookupNames;
    }

    @Override
    public PluginManager getPluginManager() {
        return pluginManager;
    }

    @Override
    public WrappedCommandMap getCommandMap() {
        return commandMap;
    }

}
