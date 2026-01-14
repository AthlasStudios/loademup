package club.athlas.loademup.server.commands;

import club.athlas.loademup.LoadEmUp;
import club.athlas.loademup.api.constant.LEUAction;
import club.athlas.loademup.api.object.Dependency;
import club.athlas.loademup.api.utils.LEUUtils;
import club.athlas.loademup.config.constant.LangKey;
import club.athlas.loademup.utils.TagResolverUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class CommandCommons {

    public static boolean sendPluginDescValidationResult(LoadEmUp plugin, @NotNull PluginDescriptionFile desc, CommandSender sender) {
        if (desc.getName().isEmpty()) {
            plugin.getMessages().send(sender, LangKey.MISSING_PLUGIN_NAME);
            return false;
        }

        if (desc.getMain().isEmpty()) {
            plugin.getMessages().send(sender, LangKey.MISSING_MAIN_CLASS);
            return false;
        }

        if (desc.getVersion().isEmpty()) {
            plugin.getMessages().send(sender, LangKey.MISSING_PLUGIN_VERSION);
            return false;
        }

        return true;
    }

    public static boolean sendClassLoaderValidationResult(@NotNull LoadEmUp plugin, @NotNull File file, CommandSender sender) {
        return switch (plugin.getLoader().validateClassLoader(file)) {
            case FILE_NOT_FOUND -> {
                plugin.getMessages().send(sender, LangKey.JAR_NOT_FOUND, Placeholder.parsed("file", file.getName()));
                yield false;
            }

            case MISSING_PLUGIN_DESCRIPTION -> {
                plugin.getMessages().send(sender, LangKey.MISSING_PLUGIN_DESCRIPTION, Placeholder.parsed("file", file.getName()));
                yield false;
            }

            case NO_MAIN_CLASS -> {
                plugin.getMessages().send(sender, LangKey.NO_MAIN_CLASS, Placeholder.parsed("file", file.getName()));
                yield false;
            }

            case INVALID_MAIN_CLASS -> {
                plugin.getMessages().send(sender, LangKey.INVALID_MAIN_CLASS, Placeholder.parsed("file", file.getName()));
                yield false;
            }

            case UNEXPECTED_ERROR -> {
                plugin.getMessages().send(sender, LangKey.UNEXPECTED_ERROR, Placeholder.parsed("file", file.getName()));
                yield false;
            }

            case VALID -> true;
        };
    }

    public static boolean sendMissingDepsResult(@NotNull LoadEmUp plugin, PluginDescriptionFile desc, CommandSender sender) {
        List<String> missingDeps = plugin.getLoader().getMissingDependencies(desc).stream()
                .map(Dependency::name)
                .collect(Collectors.toList());

        if (missingDeps.isEmpty()) return true;

        plugin.getMessages().send(sender, LangKey.MISSING_DEPENDENCIES, TagResolverUtils.listPlaceholder("dependencies", missingDeps));

        return false;
    }

    public static boolean sendUnsupportedAction(@NotNull LoadEmUp plugin, LEUAction action, File file, CommandSender sender) {
        if (LEUUtils.isSupported(file, action)) return true;

        plugin.getMessages().send(
                sender,
                LangKey.UNSUPPORTED_PLUGIN_ACTION,
                Placeholder.parsed("action", action.name())
        );

        return false;
    }

    public static boolean sendUnsupportedAction(@NotNull LoadEmUp plugin, LEUAction action, Plugin target, CommandSender sender) {
        return plugin.getLoader().getPluginFile(target).map(file -> sendUnsupportedAction(plugin, action, file, sender)).orElse(true);
    }

//    public static boolean hasArgFlags(@NotNull CommandArguments args, String... flags) {
//        return Arrays.stream(args.getRawArgs())
//                .anyMatch(arg -> Arrays.asList(flags).contains(arg));
//    }

}
