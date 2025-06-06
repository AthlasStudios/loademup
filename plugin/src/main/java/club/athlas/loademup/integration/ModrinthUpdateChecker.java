package club.athlas.loademup.integration;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.BiConsumer;

public class ModrinthUpdateChecker {

    private final Plugin plugin;
    private final String slug;

    private String latestCachedVersion;

    public ModrinthUpdateChecker(Plugin plugin, String slug) {
        this.plugin = plugin;
        this.slug = slug;
    }

    public void getVersion(final BiConsumer<String, Boolean> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            Gson gson = new GsonBuilder().create();

            try {
                URI url = new URI("https://api.modrinth.com/v2/project/" + slug + "/version");
                HttpURLConnection connection = (HttpURLConnection) url.toURL().openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                JsonElement jsonElement = gson.fromJson(reader, JsonElement.class);

                if (jsonElement == null) {
                    consumer.accept(null, false);
                    return;
                }

                JsonArray json = jsonElement.getAsJsonArray();

                reader.close();
                connection.disconnect();

                JsonObject latest = json.get(0).getAsJsonObject();
                String version = latest.get("version_number").getAsString();

                consumer.accept(version, true);
                latestCachedVersion = version;

            } catch (FileNotFoundException ignored) {
                consumer.accept("0.0.0", false);
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void isLatest(final BiConsumer<Boolean, String> consumer) {
        getVersion((latestVersion, status) -> consumer.accept(getCurrentVersion().equals(latestVersion), latestVersion));
    }

    public void cacheLatestVersion(final BiConsumer<String, Boolean> consumer) {
        getVersion((latest, status) -> {
            if (status) latestCachedVersion = latest;
            consumer.accept(latest, status);
        });
    }

    public void cacheLatestVersion() {
        cacheLatestVersion((s, aBoolean) -> plugin.getLogger().info("[Update Checker] Cached latest version of " + plugin.getName() + "."));
    }

    public String getCurrentVersion() {
        return plugin.getDescription().getVersion();
    }

    public String getLatestCachedVersion() {
        return latestCachedVersion == null ? "\uD83D\uDD04" : latestCachedVersion;
    }

}
