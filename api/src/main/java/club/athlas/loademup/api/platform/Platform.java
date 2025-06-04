package club.athlas.loademup.api.platform;

import org.jetbrains.annotations.Nullable;

public interface Platform {

    String getIdentifier();

    @Nullable
    String getFinderClassPath();

}
