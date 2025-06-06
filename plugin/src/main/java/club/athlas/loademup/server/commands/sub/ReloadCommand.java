package club.athlas.loademup.server.commands.sub;

import club.athlas.loademup.LoadEmUp;
import club.athlas.loademup.api.constant.LEUPermission;
import club.athlas.loademup.api.loader.PluginLoader;
import club.athlas.loademup.api.object.PluginData;
import club.athlas.loademup.api.utils.FileUtils;
import club.athlas.loademup.config.constant.LangKey;
import club.athlas.loademup.server.commands.CommandCommons;
import club.athlas.loademup.utils.TagResolverUtils;
import me.gurwi.commandframework.arguments.BooleanArgument;
import me.gurwi.commandframework.arguments.StringArgument;
import me.gurwi.commandframework.usable.SubCommand;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.plugin.Plugin;

import java.util.stream.Collectors;

public class ReloadCommand extends SubCommand<LoadEmUp> {

    private final PluginLoader loader;

    public ReloadCommand(LoadEmUp plugin) {
        super(plugin, "reload");

        this.loader = plugin.getLoader();
    }

    @Override
    public void setup() {
        withPermissions(LEUPermission.RELOAD_COMMAND);

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
                        TagResolver pluginDataPlaceholder = TagResolverUtils.resolverFrom(PluginData.from(targetPlugin));
                        plugin.getMessages().send(sender, LangKey.LOOKING_FOR_NEW_JAR_FILE, pluginDataPlaceholder);

                        loader.getPluginFile(targetPlugin).ifPresentOrElse(
                                targetFile -> {
                                    if (!targetFile.exists()) {
                                        plugin.getMessages().send(sender, LangKey.JAR_NOT_FOUND, Placeholder.parsed("file", targetFile.getName()));
                                        return;
                                    }

                                    plugin.getMessages().send(sender, LangKey.LOOKING_FOR_PLUGIN_YML, Placeholder.parsed("file", targetFile.getName()));
                                    plugin.getMessages().send(sender, LangKey.INTEGRITY_CHECK_START, Placeholder.parsed("file", targetFile.getName()));

                                    if (!FileUtils.validateJarIntegrity(targetFile)) {
                                        plugin.getMessages().send(sender, LangKey.INVALID_PLUGIN_JAR, Placeholder.parsed("file", targetFile.getName()));
                                        return;
                                    }

                                    loader.getDescriptionFile(targetFile).ifPresentOrElse(
                                            desc -> {
                                                if (!CommandCommons.sendPluginDescValidationResult(plugin, desc, sender)) return;

                                                TagResolver updatedPluginDataPlaceholder = TagResolverUtils.resolverFrom(PluginData.from(desc));

                                                if (!CommandCommons.sendClassLoaderValidationResult(plugin, targetFile, sender)) return;

                                                plugin.getMessages().send(sender, LangKey.CHECKING_DEPENDENCIES, updatedPluginDataPlaceholder);

                                                if (!CommandCommons.sendMissingDepsResult(plugin, desc, sender)) return;

                                                plugin.getMessages().send(sender, LangKey.PLUGIN_RELOAD_START, updatedPluginDataPlaceholder);

                                                long start = System.currentTimeMillis();

                                                try {
                                                    plugin.getMessages().send(sender, LangKey.PLUGIN_UNLOAD_START, updatedPluginDataPlaceholder);
                                                    loader.unload(targetPlugin);
                                                } catch (Exception e) {
                                                    plugin.getMessages().send(sender, LangKey.PLUGIN_RELOAD_FAIL, Placeholder.parsed("error_msg", e.getMessage()));
                                                    e.printStackTrace();
                                                }

                                                try {
                                                    plugin.getMessages().send(sender, LangKey.PLUGIN_LOAD_START, updatedPluginDataPlaceholder);

                                                    loader.load(targetFile).ifPresent(loaded -> {
                                                        long time = System.currentTimeMillis() - start;

                                                        if (loaded.isEnabled()) {
                                                            plugin.getMessages().send(sender, LangKey.PLUGIN_RELOADED, updatedPluginDataPlaceholder, TagResolverUtils.resolverFromTime(time));
                                                            return;
                                                        }

                                                        plugin.getMessages().send(sender, LangKey.PLUGIN_LOADED_BUT_DISABLED, updatedPluginDataPlaceholder, TagResolverUtils.resolverFromTime(time));
                                                    });

                                                } catch (Exception e) {
                                                    plugin.getMessages().send(sender, LangKey.PLUGIN_RELOAD_FAIL, Placeholder.parsed("error_msg", e.getMessage()));
                                                    e.printStackTrace();
                                                }

                                            },

                                            () -> plugin.getMessages().send(sender, LangKey.MISSING_PLUGIN_DESCRIPTION, Placeholder.parsed("file", targetFile.getName()))
                                    );
                                },
                                () -> plugin.getMessages().send(sender, LangKey.MISSING_NEW_JAR_FILE, pluginDataPlaceholder)
                        );

                    },

                    () -> plugin.getMessages().send(sender, LangKey.PLUGIN_NOT_FOUND, Placeholder.parsed("name", pluginName))
            );

            sender.sendMessage("\n");
        });
    }

}
