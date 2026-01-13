package club.athlas.loademup.server.commands.sub;

import club.athlas.loademup.LoadEmUp;
import club.athlas.loademup.api.constant.LEUAction;
import club.athlas.loademup.api.constant.LEUPermission;
import club.athlas.loademup.api.loader.PluginLoader;
import club.athlas.loademup.api.object.Dependency;
import club.athlas.loademup.api.object.PluginData;
import club.athlas.loademup.config.constant.LangKey;
import club.athlas.loademup.server.commands.CommandCommons;
import club.athlas.loademup.server.commands.base.argument.CommandFlag;
import club.athlas.loademup.server.commands.base.argument.FlagArgument;
import club.athlas.loademup.server.commands.base.PluginCommand;
import club.athlas.loademup.server.commands.base.argument.FlagsContainer;
import club.athlas.loademup.server.commands.base.argument.PluginArgument;
import club.athlas.loademup.utils.TagResolverUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;

public class UnloadCommand extends PluginCommand<LoadEmUp> {

    private final PluginLoader loader;

    public UnloadCommand(LoadEmUp plugin) {
        super(plugin, "unload");

        this.loader = plugin.getLoader();
    }

    @Override
    public void setup() {
        withPermission(LEUPermission.UNLOAD_COMMAND);

        withArguments(
                new PluginArgument("plugin", plugin)
        );

        withOptionalArguments(
                new FlagArgument("flags")
                        .withFlags(
                                new CommandFlag("force", "Force the plugin unload even if it has dependents."),
                                new CommandFlag("unload-deps", "Unload all dependents of the plugin.")
                        )
        );

        executes((sender, args) -> {
            Plugin target = (Plugin) args.get("plugin");
            FlagsContainer flags = (FlagsContainer) args.getOptional("flags").orElse(FlagsContainer.EMPTY);

            boolean force = flags.get("force");
            boolean unloadDeps = flags.get("unload-deps");

            sender.sendMessage("\n");

            if (!CommandCommons.sendUnsupportedAction(plugin, LEUAction.UNLOAD, target, sender)) return;

            TagResolver placeholder = TagResolverUtils.resolverFrom(PluginData.from(target));

            plugin.getMessages().send(sender, LangKey.CHECKING_DEPENDENTS, placeholder);

            List<String> dependants = getAllDependentNames(loader, target);

            if (!dependants.isEmpty() && !force) {
                plugin.getMessages().send(sender, LangKey.PLUGIN_UNLOAD_BLOCKED, placeholder, TagResolverUtils.listPlaceholder("dependants", dependants));
                return;
            }

            plugin.getMessages().send(sender, LangKey.PLUGIN_UNLOAD_START, placeholder);

            unloadPlugin(target, sender);

            if (!unloadDeps) return;

            dependants.stream()
                    .map(loader::getPlugin)
                    .filter(Optional::isPresent)
                    .forEach(dependant -> unloadPlugin(dependant.get(), sender));

            sender.sendMessage("\n");
        });
    }

    private void unloadPlugin(Plugin targetPlugin, CommandSender sender) {
        TagResolver placeholder = TagResolverUtils.resolverFrom(PluginData.from(targetPlugin));

        try {
            long start = System.currentTimeMillis();
            loader.unload(targetPlugin);
            long time = System.currentTimeMillis() - start;

            plugin.getMessages().send(sender, LangKey.PLUGIN_UNLOADED, placeholder, TagResolverUtils.resolverFromTime(time));
        } catch (Exception e) {
            plugin.getMessages().send(sender, LangKey.PLUGIN_UNLOAD_FAIL, Placeholder.parsed("error_msg", e.getMessage()));
            plugin.getLogger().log(Level.WARNING, "Failed to unload plugin " + targetPlugin.getName(), e);
        }
    }

    private @NotNull List<String> getAllDependentNames(PluginLoader loader, Plugin rootPlugin) {
        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        collectDependentNames(loader, rootPlugin, visited, result);

        result.remove(rootPlugin.getName());
        return result;
    }

    private void collectDependentNames(@NotNull PluginLoader loader, Plugin plugin, Set<String> visited, List<String> result) {
        for (Dependency dependant : loader.getDependents(plugin)) {
            if (!dependant.isLoaded()) continue;

            String name = dependant.name();

            if (visited.add(name)) {
                result.add(name);
                dependant.getPlugin().ifPresent(depPlugin -> collectDependentNames(loader, depPlugin, visited, result));
            }
        }
    }

}
