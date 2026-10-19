package club.athlas.loademup.server.platform;

import club.athlas.loademup.LoadEmUp;
import club.athlas.loademup.api.platform.DefPlatform;
import club.athlas.loademup.api.platform.PlatformIntegration;
import club.athlas.loademup.api.platform.PlatformManger;
import dev.gurwi.prism.reflection.ReflectionUtil;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

public class BasePlatformManager implements PlatformManger {

    private final LoadEmUp plugin;
    private final PlatformIntegration integration;

    public BasePlatformManager(LoadEmUp plugin) {
        this.plugin = plugin;

        try {
            this.integration = load();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PlatformIntegration getIntegration() {
        return integration;
    }

    private @Nullable PlatformIntegration load() throws ClassNotFoundException {
        DefPlatform platform = DefPlatform.getCurrentPlatform();
        Class<?> integrationClass = Class.forName(platform.getIntegrationClassPath());

        plugin.getLogger().info("Trying to load " + platform.getIdentifier() + " platform...");

        try {
            return (PlatformIntegration) ReflectionUtil.instantiate(integrationClass, new Class<?>[] { JavaPlugin.class }, plugin);
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to load " + platform.getIdentifier() + " platform: " + e.getMessage(), e);
            return null;
        }
    }

}
