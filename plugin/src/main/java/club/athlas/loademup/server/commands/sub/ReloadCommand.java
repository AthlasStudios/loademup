package club.athlas.loademup.server.commands.sub;

import club.athlas.loademup.LoadEmUp;
import club.athlas.loademup.api.constant.LEUAction;
import club.athlas.loademup.api.constant.LEUPermission;
import club.athlas.loademup.api.loader.PluginLoader;
import club.athlas.loademup.api.object.Dependency;
import club.athlas.loademup.api.object.PluginData;
import club.athlas.loademup.api.utils.FileUtils;
import club.athlas.loademup.api.utils.LEUUtils;
import club.athlas.loademup.config.constant.LangKey;
import club.athlas.loademup.server.commands.CommandCommons;
import club.athlas.loademup.server.commands.base.PluginCommand;
import club.athlas.loademup.server.commands.base.argument.CommandFlag;
import club.athlas.loademup.server.commands.base.argument.FlagArgument;
import club.athlas.loademup.server.commands.base.argument.FlagsContainer;
import club.athlas.loademup.server.commands.base.argument.PluginArgument;
import club.athlas.loademup.utils.TagResolverUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ReloadCommand extends PluginCommand<LoadEmUp> {

    private final PluginLoader loader;

    public ReloadCommand(LoadEmUp plugin) {
        super(plugin, "reload");

        this.loader = plugin.getLoader();
    }

    @Override
    public void setup() {
        withPermission(LEUPermission.RELOAD_COMMAND);

        withArguments(
                new PluginArgument("plugin", plugin)
        );

        withOptionalArguments(
                new FlagArgument("flags")
                        .withFlags(
                                new CommandFlag("force", "Force reload plugin ignoring every plugin depending on it."),
                                new CommandFlag("reload-deps", "Reload all dependents of the plugin with the plugin itself.")
                        )
        );


        executes((sender, args) -> {
            Plugin target = (Plugin) args.get("plugin");
            FlagsContainer flags = (FlagsContainer) args.getOptional("flags").orElse(FlagsContainer.EMPTY);

            Boolean force = flags.get("force");
            Boolean reloadDeps = flags.get("reload-deps");

            sender.sendMessage("\n");

            if (!CommandCommons.sendUnsupportedAction(plugin, LEUAction.RELOAD, target, sender)) return;

            TagResolver pluginDataPlaceholder = TagResolverUtils.resolverFrom(PluginData.from(target));
            plugin.getMessages().send(sender, LangKey.LOOKING_FOR_NEW_JAR_FILE, pluginDataPlaceholder);

            loader.getPluginFile(target).ifPresentOrElse(
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

                                    plugin.getMessages().send(sender, LangKey.CHECKING_DEPENDENTS, updatedPluginDataPlaceholder);

                                    List<Plugin> dependants = getDependentsTopologicalOrder(loader, target);

                                    if (!dependants.isEmpty() && !(force || reloadDeps)) {
                                        plugin.getMessages().send(
                                                sender,
                                                LangKey.PLUGIN_RELOAD_BLOCKED,
                                                updatedPluginDataPlaceholder,
                                                TagResolverUtils.listPlaceholder("dependants", dependants.stream()
                                                        .map(Plugin::getName)
                                                        .collect(Collectors.toList())
                                                )
                                        );

                                        return;
                                    }

                                    if (!CommandCommons.sendClassLoaderValidationResult(plugin, targetFile, sender)) return;

                                    plugin.getMessages().send(sender, LangKey.CHECKING_DEPENDENCIES, updatedPluginDataPlaceholder);

                                    if (!CommandCommons.sendMissingDepsResult(plugin, desc, sender)) return;

                                    plugin.getMessages().send(sender, LangKey.PLUGIN_RELOAD_START, updatedPluginDataPlaceholder);

                                    long start = System.currentTimeMillis();

                                    try {
                                        plugin.getMessages().send(sender, LangKey.PLUGIN_UNLOAD_START, updatedPluginDataPlaceholder);
                                        loader.unload(target);
                                    } catch (Exception e) {
                                        plugin.getMessages().send(sender, LangKey.PLUGIN_RELOAD_FAIL, updatedPluginDataPlaceholder, Placeholder.parsed("error_msg", e.getMessage()));
                                        plugin.getLogger().log(Level.WARNING, "Failed to reload plugin " + target.getName(), e);
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

                                        if (!reloadDeps) return;

                                        long dependantsReloadStart = System.currentTimeMillis();
                                        AtomicInteger successFullyReloadedDeps = new AtomicInteger();
                                        plugin.getMessages().send(sender, LangKey.RELOADING_DEPENDANDTS, updatedPluginDataPlaceholder, Placeholder.parsed("count", String.valueOf(dependants.size())));

                                        dependants.forEach(dependant -> {
                                            Optional<File> currentJar = loader.getPluginCurrentJarInst(dependant);
                                            TagResolver dependantPlaceholder = TagResolverUtils.resolverFrom(PluginData.from(dependant), "dependant");

                                            if (currentJar.isEmpty()) {
                                                plugin.getMessages().send(sender, LangKey.DEPENDAND_CURRENT_JAR_NOT_FOUND, updatedPluginDataPlaceholder, TagResolverUtils.resolverFrom(PluginData.from(dependant)));
                                                return;
                                            }

                                            if (!LEUUtils.isSupported(currentJar.get(), LEUAction.RELOAD)) {
                                                plugin.getMessages().send(sender, LangKey.DEPENDAND_RELOAD_UNSUPPORTED, updatedPluginDataPlaceholder, TagResolverUtils.resolverFrom(PluginData.from(dependant)));
                                                return;
                                            }

                                            long dependantReloadStart = System.currentTimeMillis();

                                            try {
                                                loader.reloadFromCurrentJar(dependant);
                                                plugin.getMessages().send(sender,
                                                        LangKey.PLUGIN_DEPENDANT_RELOADED,
                                                        updatedPluginDataPlaceholder,
                                                        dependantPlaceholder,
                                                        TagResolverUtils.resolverFromTime(System.currentTimeMillis() - dependantReloadStart)
                                                );

                                                successFullyReloadedDeps.getAndIncrement();

                                            } catch (InvalidPluginException | InvalidDescriptionException e) {
                                                plugin.getMessages().send(
                                                        sender,
                                                        LangKey.PLUGIN_DEPENDANT_RELOAD_FAIL,
                                                        updatedPluginDataPlaceholder,
                                                        dependantPlaceholder,
                                                        Placeholder.parsed("error_msg", e.getMessage()),
                                                        TagResolverUtils.resolverFromTime(System.currentTimeMillis() - dependantReloadStart)
                                                );

                                                plugin.getLogger().log(Level.WARNING, "Failed to reload plugin " + target.getName() + " dependant " + dependant.getName(), e);
                                            }
                                        });

                                        plugin.getMessages().send(sender, LangKey.PLUGIN_DEPENDANTS_RELOAD_FINISH,
                                                updatedPluginDataPlaceholder,
                                                TagResolverUtils.resolverFromTime(System.currentTimeMillis() - dependantsReloadStart),
                                                Placeholder.parsed("count", String.valueOf(dependants.size())));

                                    } catch (Exception e) {
                                        plugin.getMessages().send(sender, LangKey.PLUGIN_RELOAD_FAIL, updatedPluginDataPlaceholder, Placeholder.parsed("error_msg", e.getMessage()));
                                        plugin.getLogger().log(Level.WARNING, "Failed to reload plugin " + target.getName(), e);
                                    }

                                },

                                () -> plugin.getMessages().send(sender, LangKey.MISSING_PLUGIN_DESCRIPTION, Placeholder.parsed("file", targetFile.getName()))
                        );
                    },
                    () -> plugin.getMessages().send(sender, LangKey.MISSING_NEW_JAR_FILE, pluginDataPlaceholder)
            );

            sender.sendMessage("\n");
        });
    }

    private @NotNull List<Plugin> getDependentsTopologicalOrder(PluginLoader loader, Plugin rootPlugin) {
        Set<Plugin> allDependents = new HashSet<>();
        collectAllDependents(loader, rootPlugin, allDependents);

        return topologicalSort(allDependents);
    }

    private void collectAllDependents(@NotNull PluginLoader loader, Plugin plugin, Set<Plugin> collected) {
        for (Dependency dependant : loader.getDependents(plugin)) {
            if (!dependant.isLoaded()) continue;

            dependant.getPlugin().ifPresent(depPlugin -> {
                if (collected.add(depPlugin)) collectAllDependents(loader, depPlugin, collected);
            });
        }
    }

    private @NotNull List<Plugin> topologicalSort(@NotNull Set<Plugin> plugins) {
        Map<String, Plugin> nameToPlugin = plugins.stream().collect(Collectors.toMap(Plugin::getName, p -> p));
        Map<String, List<String>> adj = new HashMap<>();

        for (Plugin plugin : plugins) {
            adj.put(plugin.getName(), new ArrayList<>());
        }

        for (Plugin plugin : plugins) {

            List<String> dependencies = getDirectDependencyNames(plugin);
            for (String depName : dependencies) {
                if (adj.containsKey(depName)) {
                    adj.get(depName).add(plugin.getName());
                }
            }
        }

        Map<String, Integer> inDegree = new HashMap<>();

        for (String name : adj.keySet()) {
            inDegree.put(name, 0);
        }

        for (List<String> deps : adj.values()) {
            for (String to : deps) {
                inDegree.put(to, inDegree.get(to) + 1);
            }
        }

        Deque<String> queue = new ArrayDeque<>();
        for (var entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) queue.add(entry.getKey());
        }

        List<Plugin> sorted = new ArrayList<>();
        while (!queue.isEmpty()) {
            String current = queue.remove();
            sorted.add(nameToPlugin.get(current));

            for (String neighbor : adj.get(current)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);

                if (inDegree.get(neighbor) == 0) queue.add(neighbor);
            }
        }

        if (sorted.size() != plugins.size()) throw new IllegalStateException("Cyclic dependency detected among plugins!");

        return sorted;
    }

    private @NotNull @Unmodifiable List<String> getDirectDependencyNames(@NotNull Plugin plugin) {
        List<String> deps = new ArrayList<>(plugin.getDescription().getDepend());
        deps.addAll(plugin.getDescription().getSoftDepend());

        return Collections.unmodifiableList(deps);
    }

}
