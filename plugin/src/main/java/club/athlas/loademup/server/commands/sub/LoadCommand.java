package club.athlas.loademup.server.commands.sub;

import club.athlas.loademup.LoadEmUp;
import club.athlas.loademup.api.constant.LEUAction;
import club.athlas.loademup.api.constant.LEUPermission;
import club.athlas.loademup.api.loader.PluginLoader;
import club.athlas.loademup.api.object.PluginData;
import club.athlas.loademup.api.utils.FileUtils;
import club.athlas.loademup.config.constant.LangKey;
import club.athlas.loademup.server.commands.CommandCommons;
import club.athlas.loademup.server.commands.base.PluginCommand;
import club.athlas.loademup.utils.TagResolverUtils;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.io.File;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class LoadCommand extends PluginCommand<LoadEmUp> {

    private final PluginLoader loader;

    public LoadCommand(LoadEmUp plugin) {
        super(plugin, "load");

        this.loader = plugin.getLoader();
    }

    @Override
    public void setup() {
        withArguments(
                new GreedyStringArgument("plugin")
                        .replaceSuggestions(ArgumentSuggestions.stringCollection(sender ->
                                loader.getPluginJarFiles().stream()
                                        .map(File::getName)
                                        .collect(Collectors.toList())
                        ))
        );

        executes((sender, args) -> {
            withPermission(LEUPermission.LOAD_COMMAND);

            String jarFileName = (String) args.get("plugin");

            sender.sendMessage("\n");

            loader.getPluginFile(jarFileName).ifPresentOrElse(
                    targetFile -> {
                        if (!CommandCommons.sendUnsupportedAction(plugin, LEUAction.LOAD, targetFile, sender)) return;

                        plugin.getMessages().send(sender, LangKey.LOOKING_FOR_PLUGIN_YML, Placeholder.parsed("file", targetFile.getName()));
                        plugin.getMessages().send(sender, LangKey.INTEGRITY_CHECK_START, Placeholder.parsed("file", targetFile.getName()));

                        if (!FileUtils.validateJarIntegrity(targetFile)) {
                            plugin.getMessages().send(sender, LangKey.INVALID_PLUGIN_JAR, Placeholder.parsed("file", targetFile.getName()));
                            return;
                        }

                        loader.getDescriptionFile(targetFile).ifPresentOrElse(
                                desc -> {
                                    if (!CommandCommons.sendPluginDescValidationResult(plugin, desc, sender)) return;

                                    TagResolver placeholder = TagResolverUtils.resolverFrom(PluginData.from(desc));

                                    if (loader.getPlugin(desc.getName()).isPresent()) {
                                        plugin.getMessages().send(sender, LangKey.PLUGIN_ALREADY_LOADED, placeholder);
                                        return;
                                    }

                                    if (!CommandCommons.sendClassLoaderValidationResult(plugin, targetFile, sender)) return;

                                    plugin.getMessages().send(sender, LangKey.CHECKING_DEPENDENCIES, placeholder);

                                    if (!CommandCommons.sendMissingDepsResult(plugin, desc, sender)) return;

                                    try {
                                        plugin.getMessages().send(sender, LangKey.PLUGIN_LOAD_START, placeholder);
                                        long start = System.currentTimeMillis();

                                        loader.load(targetFile).ifPresent(loaded -> {
                                            long time = System.currentTimeMillis() - start;

                                            if (loaded.isEnabled()) {
                                                plugin.getMessages().send(sender, LangKey.PLUGIN_LOADED, placeholder, TagResolverUtils.resolverFromTime(time));
                                                // ASK TO RELOAD SOFT DEPENDS TOO
                                                return;
                                            }

                                            plugin.getMessages().send(sender, LangKey.PLUGIN_LOADED_BUT_DISABLED, placeholder, TagResolverUtils.resolverFromTime(time));
                                        });

                                    } catch (Exception e) {
                                        plugin.getMessages().send(sender, LangKey.PLUGIN_LOAD_FAIL, Placeholder.parsed("error_msg", e.getMessage()));
                                        plugin.getLogger().log(Level.WARNING, "Unable to load plugin " + desc.getName(), e);
                                    }
                                },

                                () -> plugin.getMessages().send(sender, LangKey.MISSING_PLUGIN_DESCRIPTION, Placeholder.parsed("file", targetFile.getName()))
                        );
                    },

                    () -> plugin.getMessages().send(sender, LangKey.JAR_NOT_FOUND, Placeholder.parsed("file", jarFileName))
            );

            sender.sendMessage("\n");
        });
    }

}
