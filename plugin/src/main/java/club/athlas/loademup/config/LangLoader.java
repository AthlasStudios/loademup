package club.athlas.loademup.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.configuration.Configuration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record LangLoader(Configuration mainConfig, Configuration langConfig) {

    @Contract(value = "_ -> new", pure = true)
    public @NotNull TextComponent getComponent(Component component) {
        return Component.text("TODO");
    }

}
