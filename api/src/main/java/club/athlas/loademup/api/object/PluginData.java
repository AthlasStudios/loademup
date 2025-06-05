package club.athlas.loademup.api.object;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record PluginData(String name, String version, List<String> authors, List<String> contributors, @Nullable String description, @Nullable String website, String apiVersion, boolean paperPlugin) {

    @Contract("_ -> new")
    public static @NotNull PluginData from(@NotNull PluginDescriptionFile desc) {
        String name = desc.getName();
        String version = desc.getVersion();
        List<String> authors = desc.getAuthors();
        List<String> contributors = desc.getContributors();
        String description = desc.getDescription();
        String website = desc.getWebsite();
        String apiVersion = "Legacy";

        try {
            PluginDescriptionFile.class.getMethod("getAPIVersion");
            apiVersion = desc.getAPIVersion();
        } catch (NoSuchMethodException ignored) { }

        return new PluginData(name, version, authors, contributors, description, website, apiVersion, false);
    }

    @SuppressWarnings("deprecation")
    @Contract("_ -> new")
    public static @NotNull PluginData from(@NotNull Plugin plugin) {
        return from(plugin.getDescription());
    }

}
