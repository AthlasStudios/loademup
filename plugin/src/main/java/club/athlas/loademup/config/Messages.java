package club.athlas.loademup.config;

import club.athlas.loademup.config.base.YamlFileHandle;
import club.athlas.loademup.config.constant.LangKey;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public final class Messages {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    private final Configuration langConfig;
    private final BukkitAudiences adventure;

    private final Map<String, String> palette;

    private Messages(Configuration langConfig, BukkitAudiences adventure) {
        this.langConfig = langConfig;
        this.adventure = adventure;
        this.palette = initPalette();
    }

    @Contract("_, _, _ -> new")
    public static @NotNull Messages initFromFile(Plugin plugin, BukkitAudiences adventure, @NotNull String filePath) {
        YamlFileHandle customConfig = YamlFileHandle.fromPath(plugin, filePath);

        if (!customConfig.getFile().exists()) {
            plugin.getLogger().log(Level.SEVERE, "[Lang] Couldn't load language! Invalid lang file. Using the default one -> EN_us");
            customConfig = YamlFileHandle.fromResource(plugin, "lang/EN_us.yml");
        }

        plugin.getLogger().log(Level.INFO, "[Lang] Language loaded from file " + customConfig.getFile().getName());

        return new Messages(customConfig.getConfig(), adventure);
    }

    @Contract(value = "_, _ -> new", pure = true)
    public @NotNull Component getMessage(LangKey langKey, TagResolver... tagResolver) {
        return getPrefix().append(getComponent(langKey, tagResolver));
    }

    @Contract(value = "_ -> new", pure = true)
    public @NotNull Component getMessage(LangKey langKey) {
        return getPrefix().append(getComponent(langKey, TagResolver.empty()));
    }

    @Contract(" -> new")
    public @NotNull Component getPrefix() {
        return getComponent(LangKey.PREFIX, TagResolver.empty());
    }

    public @NotNull Component getComponent(@NotNull LangKey key, TagResolver... tagResolvers) {
        String rawMessage = langConfig.getString(key.getPath(), "");

        TagResolver paletteResolver = TagResolver.resolver(
                palette.entrySet().stream()
                        .map(entry -> Placeholder.parsed(entry.getKey(), entry.getValue()))
                        .toArray(TagResolver.Single[]::new)
        );

        return miniMessage.deserialize(rawMessage, TagResolver.resolver(paletteResolver, TagResolver.resolver(tagResolvers)));
    }

    public void send(CommandSender sender, LangKey key, TagResolver... resolvers) {
        adventure.sender(sender).sendMessage(getMessage(key, resolvers));
    }

    public void send(CommandSender sender, LangKey key) {
        send(sender, key, TagResolver.empty());
    }

    public boolean isEmpty(@NotNull LangKey key) {
        return langConfig.getString(key.getPath(), "").isEmpty();
    }

    private @NotNull Map<String, String> initPalette() {
        Map<String, String> palette = new HashMap<>();
        ConfigurationSection paletteSection = langConfig.getConfigurationSection("vars");

        if (paletteSection == null) return palette;

        paletteSection.getKeys(false).forEach(key -> {
            String serializedComponent = paletteSection.getString(key);
            if (serializedComponent == null) return;

            palette.put(key.toLowerCase(), serializedComponent);
        });

        return palette;
    }

}
