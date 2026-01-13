package club.athlas.loademup.server.commands.base.argument;

import club.athlas.loademup.LoadEmUp;
import club.athlas.loademup.api.loader.PluginLoader;
import club.athlas.loademup.config.constant.LangKey;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.IStringTooltip;
import dev.jorel.commandapi.StringTooltip;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class PluginArgument extends SafeOverrideableArgument<Plugin, String> {

    private final LoadEmUp plugin;
    private final PluginLoader loader;

    public PluginArgument(String nodeName, @NotNull LoadEmUp plugin) {
        super(
                nodeName,
                StringArgumentType.string(),
                pluginName -> pluginName
        );

        this.plugin = plugin;
        this.loader = plugin.getLoader();

        this.replaceSuggestions(
                ArgumentSuggestions.stringsWithTooltips(info ->
                        loader.getPlugins().stream()
                                .map(p -> StringTooltip.ofString(
                                        p.getName(),
                                        p.getDescription().getDescription()
                                ))
                                .toArray(IStringTooltip[]::new)
                )
        );
    }

    @Override
    public Class<Plugin> getPrimitiveType() {
        return Plugin.class;
    }

    @Override
    public CommandAPIArgumentType getArgumentType() {
        return CommandAPIArgumentType.PRIMITIVE_STRING;
    }

    @Override
    public <Source> Plugin parseArgument(@NotNull CommandContext<Source> cmdCtx, String key, CommandArguments previousArgs) throws CommandSyntaxException {
        String input = cmdCtx.getArgument(key, String.class);

        return loader.getPlugin(input)
                .orElseThrow(() -> CommandAPI.failWithString(
                        LegacyComponentSerializer.legacySection().serialize(
                                Component.newline()
                                        .append(plugin.getMessages().getMessage(LangKey.PLUGIN_NOT_FOUND, Placeholder.parsed("name", input)))
                                        .appendNewline()
                        )
                ).getException());
    }

}
