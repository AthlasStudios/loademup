package club.athlas.loademup.api.platform;

import org.jetbrains.annotations.NotNull;

public interface PlatformManger {

    void registerPlatform(@NotNull PlatformIntegration @NotNull ... platforms);

    PlatformIntegration getCurrentPlatform();

}
