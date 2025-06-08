package club.athlas.loademup.api.platform;

import org.jetbrains.annotations.Nullable;

public enum DefPlatform implements Platform {

    SPIGOT("spigot", null),
    PAPER("paper", "io.papermc.paper.plugin.manager.PaperPluginManagerImpl"),
    PURPUR("purpur", "io.papermc.paper.plugin.manager.PaperPluginManagerImpl");

    private final String identifier;
    private final String classPath;

    DefPlatform(String identifier, String classPath) {
        this.identifier = identifier;
        this.classPath = classPath;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    @Nullable
    public String getFinderClassPath() {
        return classPath;
    }

    public static Platform getCurrentPlatform() {
        for (Platform value : DefPlatform.values()) {
            if (value.getFinderClassPath() == null || value.getFinderClassPath().isEmpty()) continue;

            try {
                Class.forName(value.getFinderClassPath());
                return value;
            } catch (ClassNotFoundException ignored) { }
        }

        return SPIGOT;
    }

}
