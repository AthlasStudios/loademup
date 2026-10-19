package club.athlas.loademup.server.commands.sub;

import club.athlas.loademup.LoadEmUp;
import club.athlas.loademup.api.constant.LEUAction;
import club.athlas.loademup.api.constant.LEUPermission;
import club.athlas.loademup.api.loader.PluginLoader;
import club.athlas.loademup.api.object.PluginData;
import club.athlas.loademup.config.constant.LangKey;
import club.athlas.loademup.server.commands.CommandCommons;
import club.athlas.loademup.server.commands.argument.PluginArgument;
import club.athlas.loademup.server.commands.base.PluginCommand;
import club.athlas.loademup.utils.TagResolverUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class EnableCommand extends PluginCommand<LoadEmUp> {

    private final PluginLoader loader;

    public EnableCommand(LoadEmUp plugin) {
        super(plugin, "enable");

        this.loader = plugin.getLoader();
    }

    @Override
    public void setup() {
        withDescription("Enable a plugin.");
        withPermission(LEUPermission.ENABLE_COMMAND);

        withArguments(
                new PluginArgument(plugin, "plugin")
        );

        executes((sender, args) -> {
            Plugin target = args.get("plugin");

            sender.sendMessage("\n");

            if (!CommandCommons.sendUnsupportedAction(plugin, LEUAction.LOAD, target, sender)) return;

            TagResolver placeholder = TagResolverUtils.resolverFrom(PluginData.from(target));

            if (target.isEnabled()) {
                plugin.getMessages().send(sender, LangKey.PLUGIN_ALREADY_ENABLED, placeholder);
                return;
            }

            try {
                plugin.getMessages().send(sender, LangKey.PLUGIN_ENABLE_START, placeholder);
                long start = System.currentTimeMillis();

                loader.enablePlugin(target);
                long end = System.currentTimeMillis() - start;

                plugin.getMessages().send(sender, LangKey.PLUGIN_ENABLED, placeholder, TagResolverUtils.resolverFromTime(end));
            } catch (Exception e) {
                plugin.getMessages().send(sender, LangKey.PLUGIN_ENABLE_FAIL, Placeholder.parsed("error_msg", e.getMessage()));
                plugin.getLogger().log(Level.WARNING, "Failed to enable plugin " + target.getName(), e);
            }

            sender.sendMessage("\n");
        });
    }

}