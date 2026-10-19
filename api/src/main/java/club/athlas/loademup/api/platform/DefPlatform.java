package club.athlas.loademup.api.platform;

import org.jetbrains.annotations.Nullable;

public enum DefPlatform implements Platform {

    SPIGOT("spigot", null, "club.athlas.loademup.spigot.SpigotPlatform"),
    PAPER("paper", "io.papermc.paper.plugin.lifecycle.event.LifecycleEventOwner", "club.athlas.loademup.paper.PaperPlatform"),
    LEGACY_PAPER("legacy-paper", "io.papermc.paper.plugin.manager.PaperPluginManagerImpl", "club.athlas.loademup.spigot.wrapper.LegacyPaperPlatform");

    private final String identifier;
    private final String finderClassPath;
    private final String integrationClassPath;

    DefPlatform(String identifier, @Nullable String finderClassPath, @Nullable String integrationClassPath) {
        this.identifier = identifier;
        this.finderClassPath = finderClassPath;
        this.integrationClassPath = integrationClassPath;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    @Nullable
    public String getFinderClassPath() {
        return finderClassPath;
    }

    public String getIntegrationClassPath() {
        return integrationClassPath;
    }

    public static DefPlatform getCurrentPlatform() {
        for (DefPlatform value : DefPlatform.values()) {
            if (value.getFinderClassPath() == null || value.getFinderClassPath().isEmpty()) continue;

            try {
                Class.forName(value.getFinderClassPath());
                return value;
            } catch (ClassNotFoundException ignored) { }
        }

        return SPIGOT;
    }

}
