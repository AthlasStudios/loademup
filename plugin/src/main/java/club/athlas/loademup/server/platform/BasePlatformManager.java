package club.athlas.loademup.server.platform;

import club.athlas.loademup.api.platform.DefPlatform;
import club.athlas.loademup.api.platform.Platform;
import club.athlas.loademup.api.platform.PlatformIntegration;
import club.athlas.loademup.api.platform.PlatformManger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BasePlatformManager implements PlatformManger {

    private final Map<Platform, PlatformIntegration> platformIntegrations = new HashMap<>();
    private PlatformIntegration currentPlatform = null;

    @Override
    public void registerPlatform(@NotNull PlatformIntegration @NotNull ... platforms) {
        for (PlatformIntegration platform : platforms) {
            platformIntegrations.put(platform.getPlatform(), platform);
        }
    }

    @Override
    public PlatformIntegration getCurrentPlatform() {
        if (currentPlatform == null) currentPlatform = platformIntegrations.get(DefPlatform.SPIGOT);
        return currentPlatform;
    }

}
