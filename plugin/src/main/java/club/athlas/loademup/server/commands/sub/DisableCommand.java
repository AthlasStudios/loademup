package club.athlas.loademup.server.commands.sub;

import club.athlas.loademup.LoadEmUp;
import club.athlas.loademup.api.constant.LEUAction;
import club.athlas.loademup.api.constant.LEUPermission;
import club.athlas.loademup.api.loader.PluginLoader;
import club.athlas.loademup.api.object.PluginData;
import club.athlas.loademup.config.constant.LangKey;
import club.athlas.loademup.server.commands.CommandCommons;
import club.athlas.loademup.utils.TagResolverUtils;
import me.gurwi.commandframework.arguments.StringArgument;
import me.gurwi.commandframework.usable.SubCommand;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;
import java.util.stream.Collectors;

public class DisableCommand extends SubCommand<LoadEmUp> {

    private final PluginLoader loader;

    public DisableCommand(LoadEmUp plugin) {
        super(plugin, "disable");

        this.loader = plugin.getLoader();
    }

    @Override
    public void setup() {
        withPermissions(LEUPermission.DISABLE_COMMAND);

        withArguments(
                new StringArgument("plugin")
                        .replaceSuggestions(commandSender -> loader.getPlugins().stream()
                                .map(Plugin::getName)
                                .collect(Collectors.toList())
                        )
        );

        executes((sender, args) -> {
            String pluginName = (String) args.get("plugin");

            sender.sendMessage("\n");

            loader.getPlugin(pluginName).ifPresentOrElse(
                    targetPlugin -> {
                        if (!CommandCommons.sendUnsupportedAction(plugin, LEUAction.LOAD, targetPlugin, sender)) return;

                        TagResolver placeholder = TagResolverUtils.resolverFrom(PluginData.from(targetPlugin));

                        if (!targetPlugin.isEnabled()) {
                            plugin.getMessages().send(sender, LangKey.PLUGIN_ALREADY_DISABLED, placeholder);
                            return;
                        }

                        try {
                            plugin.getMessages().send(sender, LangKey.PLUGIN_DISABLE_START, placeholder);
                            long start = System.currentTimeMillis();

                            loader.disablePlugin(targetPlugin);
                            long end = System.currentTimeMillis() - start;

                            plugin.getMessages().send(sender, LangKey.PLUGIN_DISABLED, placeholder, TagResolverUtils.resolverFromTime(end));
                        } catch (Exception e) {
                            plugin.getMessages().send(sender, LangKey.PLUGIN_DISABLE_FAIL, Placeholder.parsed("error_msg", e.getMessage()));
                            plugin.getLogger().log(Level.WARNING, "Failed to disable plugin " + targetPlugin.getName(), e);
                        }
                    },

                    () -> plugin.getMessages().send(sender, LangKey.PLUGIN_NOT_FOUND, Placeholder.parsed("name", pluginName))
            );

            sender.sendMessage("\n");
        });
    }

}
