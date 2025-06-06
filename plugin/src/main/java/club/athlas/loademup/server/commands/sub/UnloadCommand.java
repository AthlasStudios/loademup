package club.athlas.loademup.server.commands.sub;

import club.athlas.loademup.LoadEmUp;
import club.athlas.loademup.api.constant.LEUPermission;
import club.athlas.loademup.api.loader.PluginLoader;
import club.athlas.loademup.api.object.PluginData;
import club.athlas.loademup.config.constant.LangKey;
import club.athlas.loademup.utils.TagResolverUtils;
import me.gurwi.commandframework.arguments.BooleanArgument;
import me.gurwi.commandframework.arguments.StringArgument;
import me.gurwi.commandframework.usable.SubCommand;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.plugin.Plugin;

import java.util.stream.Collectors;

public class UnloadCommand extends SubCommand<LoadEmUp> {

    private final PluginLoader loader;

    public UnloadCommand(LoadEmUp plugin) {
        super(plugin, "unload");

        this.loader = plugin.getLoader();
    }

    @Override
    public void setup() {
        withPermissions(LEUPermission.UNLOAD_COMMAND);

        withArguments(
                new StringArgument("plugin")
                        .replaceSuggestions(commandSender -> loader.getPlugins().stream()
                                .map(Plugin::getName)
                                .collect(Collectors.toList())
                        )
        );

        withOptionalArguments(
                new BooleanArgument("reload-deps")
        );

        executes((sender, args) -> {
            String pluginName = (String) args.get("plugin");
            boolean reloadDependants = args.getOptional("reload-deps")
                    .map(o -> (boolean) o)
                    .orElse(false);

            sender.sendMessage("\n");

            loader.getPlugin(pluginName).ifPresentOrElse(
                    targetPlugin -> {
                        TagResolver placeholder = TagResolverUtils.resolverFrom(PluginData.from(targetPlugin));
                        plugin.getMessages().send(sender, LangKey.PLUGIN_UNLOAD_START, placeholder);

                        long start = System.currentTimeMillis();

                        try {
                            loader.unload(targetPlugin);
                            long time = System.currentTimeMillis() - start;

                            plugin.getMessages().send(sender, LangKey.PLUGIN_UNLOADED, placeholder, TagResolverUtils.resolverFromTime(time));
                        } catch (Exception e) {
                            plugin.getMessages().send(sender, LangKey.PLUGIN_UNLOAD_FAIL, Placeholder.parsed("error_msg", e.getMessage()));
                            e.printStackTrace();
                        }
                    },

                    () -> plugin.getMessages().send(sender, LangKey.PLUGIN_NOT_FOUND, Placeholder.parsed("name", pluginName))
            );

            sender.sendMessage("\n");
        });
    }

}
