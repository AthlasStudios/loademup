package club.athlas.loademup.server.wrapper;

import club.athlas.loademup.utils.ReflectionUtils;
import io.papermc.paper.plugin.manager.PaperPluginManagerImpl;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class PaperPluginInstanceManager implements WrappedInstanceManager {

    private final List<Plugin> plugins;
    private final Map<String, Plugin> lookupNames;

    private final PluginManager pluginManager;
    private final WrappedCommandMap commandMap;

    public PaperPluginInstanceManager(PaperPluginManagerImpl pluginManager) throws NoSuchFieldException, IllegalAccessException {
        this.pluginManager = pluginManager;

        Object instanceManager = ReflectionUtils.accessField(
                PaperPluginManagerImpl.getInstance(),
                "instanceManager"
        );

        this.plugins = ReflectionUtils.accessField(
                instanceManager,
                "plugins"
        );

        this.lookupNames = ReflectionUtils.accessField(
                instanceManager,
                "lookupNames"
        );

        this.commandMap = new WrappedCommandMap(
                ReflectionUtils.accessField(
                        instanceManager,
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
