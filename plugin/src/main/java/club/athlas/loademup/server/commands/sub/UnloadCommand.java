package club.athlas.loademup.server.commands.sub;

import club.athlas.loademup.LoadEmUp;
import club.athlas.loademup.api.constant.LEUAction;
import club.athlas.loademup.api.constant.LEUPermission;
import club.athlas.loademup.api.loader.PluginLoader;
import club.athlas.loademup.api.object.Dependency;
import club.athlas.loademup.api.object.PluginData;
import club.athlas.loademup.api.utils.LEUUtils;
import club.athlas.loademup.config.constant.LangKey;
import club.athlas.loademup.server.commands.CommandCommons;
import club.athlas.loademup.utils.TagResolverUtils;
import me.gurwi.commandframework.arguments.StringArgument;
import me.gurwi.commandframework.usable.SubCommand;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;
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

        executes((sender, args) -> {
            String pluginName = (String) args.get("plugin");

            sender.sendMessage("\n");

            loader.getPlugin(pluginName).ifPresentOrElse(
                    targetPlugin -> {
                        if (!CommandCommons.sendUnsupportedAction(plugin, LEUAction.UNLOAD, targetPlugin, sender)) return;

                        TagResolver placeholder = TagResolverUtils.resolverFrom(PluginData.from(targetPlugin));

                        plugin.getMessages().send(sender, LangKey.CHECKING_DEPENDENTS, placeholder);

                        List<String> dependants = getAllDependentNames(loader, targetPlugin);

                        if (!dependants.isEmpty() && !CommandCommons.hasArgFlags(args, "--force")) {
                            plugin.getMessages().send(sender, LangKey.PLUGIN_UNLOAD_BLOCKED, placeholder, TagResolverUtils.listPlaceholder("dependants", dependants));
                            return;
                        }

                        plugin.getMessages().send(sender, LangKey.PLUGIN_UNLOAD_START, placeholder);

                        unloadPlugin(targetPlugin, sender);

                        dependants.stream()
                                .map(loader::getPlugin)
                                .filter(Optional::isPresent)
                                .forEach(dependant -> unloadPlugin(dependant.get(), sender));
                    },

                    () -> plugin.getMessages().send(sender, LangKey.PLUGIN_NOT_FOUND, Placeholder.parsed("name", pluginName))
            );

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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String @NotNull [] args) {
        if (args.length <= 1) return super.onTabComplete(sender, command, alias, args);
        return List.of("--force");
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
