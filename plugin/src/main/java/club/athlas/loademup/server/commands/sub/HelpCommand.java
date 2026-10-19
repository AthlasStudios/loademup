package club.athlas.loademup.server.commands.sub;

import club.athlas.loademup.LoadEmUp;
import club.athlas.loademup.api.constant.LEUPermission;
import club.athlas.loademup.config.Messages;
import club.athlas.loademup.config.constant.LangKey;
import club.athlas.loademup.server.commands.base.PluginCommand;
import club.athlas.loademup.utils.BasicFunctions;
import club.athlas.loademup.utils.TagResolverUtils;
import dev.gurwi.prism.argument.AbstractArgument;
import dev.gurwi.prism.argument.AbstractTreeArgument;
import dev.gurwi.prism.argument.CommandArguments;
import dev.gurwi.prism.bukkit.argument.LiteralArgument;
import dev.gurwi.prism.bukkit.argument.base.BukkitArgument;
import dev.gurwi.prism.bukkit.command.BukkitCommandDefinition;
import dev.gurwi.prism.bukkit.command.executor.BukkitCommandExecutor;
import dev.gurwi.prism.bukkit.permission.BukkitPermission;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HelpCommand extends PluginCommand<LoadEmUp> {

    private final Messages messages;

    public HelpCommand(LoadEmUp plugin) {
        super(plugin, "help");

        this.messages = plugin.getMessages();
    }

    @Override
    public void setup() {
        withDescription("Display help for LoadEmUp commands.");

        executes((sender, args) -> {
            Component helpMessage = getHelpMessage(sender, args);
            plugin.getAdventure().sender(sender).sendMessage(helpMessage);
        });

    }

    private @NotNull List<HelpValue> getHelpValues(@NotNull CommandSender sender, @NotNull CommandArguments<BukkitCommandDefinition, ?> args) {
        boolean fullPerm = sender.hasPermission(LEUPermission.FULL_PERM);

        List<? extends CommandArguments<BukkitCommandDefinition, ?>> chain = args.getChain();
        BukkitCommandDefinition root = chain.get(0).getDefinition();

        List<BukkitCommandDefinition> children = new ArrayList<>(root.getChildren());
        children.sort(Comparator.comparing(BukkitCommandDefinition::getName));

        List<HelpValue> values = new ArrayList<>();

        for (BukkitCommandDefinition child : children) {
            if (!fullPerm && !reachable(child, sender)) {
                continue;
            }

            String childCommand = root.getName() + " " + child.getName() + arguments(child);

            List<String> childBranches = new ArrayList<>();
            collectBranches(child.getArgumentTreeBranches(), childCommand, sender, fullPerm, childBranches);

            if (fullPerm || child.hasExecutor(sender) || childBranches.isEmpty()) {
                values.add(new HelpValue(childCommand, child.getDescription()));
            }

            for (String usage : childBranches) {
                values.add(new HelpValue(usage, null));
            }
        }

        List<String> rootBranches = new ArrayList<>();
        collectBranches(root.getArgumentTreeBranches(), root.getName(), sender, fullPerm, rootBranches);

        for (String usage : rootBranches) {
            values.add(new HelpValue(usage, null));
        }

        return values;
    }

    private @NotNull Component getHelpMessage(CommandSender sender, @NotNull CommandArguments<BukkitCommandDefinition, ?> args) {
        TagResolver.Single[] commonPlaceholders = {
                Placeholder.component("build", BasicFunctions.getUpdateCheckerComponent(plugin.getUpdateChecker())),
                Placeholder.parsed("version", plugin.getDescription().getVersion()),
                Placeholder.parsed("latest", plugin.getUpdateChecker().getLatestCachedVersion())
        };

        TextComponent.Builder builder = Component.text();

        if (!messages.isEmpty(LangKey.HELP_MESSAGE_HEADER)) {
            builder.append(messages.getComponent(LangKey.HELP_MESSAGE_HEADER, commonPlaceholders));
        }

        if (!messages.isEmpty(LangKey.HELP_MESSAGE_COMMAND_FORMAT)) {
            List<HelpValue> visibleHelpValues = getHelpValues(sender, args);

            for (int i = 0; i < visibleHelpValues.size(); i++) {
                HelpValue helpValue = visibleHelpValues.get(i);
                Component line = messages.getComponent(LangKey.HELP_MESSAGE_COMMAND_FORMAT, TagResolverUtils.resolverFrom(helpValue))
                        .clickEvent(ClickEvent.suggestCommand(suggestion(helpValue.getUsage())));
                builder.append(line);
                if (i < visibleHelpValues.size() - 1) builder.appendNewline();
            }
        }

        if (!messages.isEmpty(LangKey.HELP_MESSAGE_FOOTER)) {
            builder.append(messages.getComponent(LangKey.HELP_MESSAGE_FOOTER, commonPlaceholders));
        }

        return builder.build();
    }

    private static @NotNull String suggestion(@NotNull String usage) {
        int cut = usage.length();

        int angle = usage.indexOf('<');
        if (angle != -1) cut = angle;

        int square = usage.indexOf('[');
        if (square != -1) cut = Math.min(cut, square);

        String base = usage.substring(0, cut).stripTrailing();
        String suggestion = "/" + base;

        return cut < usage.length() ? suggestion + " " : suggestion;
    }

    private static boolean accessible(@NotNull BukkitCommandDefinition definition, @NotNull CommandSender sender) {
        return definition.getRequirement().test(sender)
                && definition.getPermissions().stream().allMatch(permission -> permission.hasPermission(sender));
    }

    private static boolean reachable(@NotNull BukkitCommandDefinition definition, @NotNull CommandSender sender) {
        if (!accessible(definition, sender)) {
            return false;
        }

        if (definition.hasExecutor(sender)) {
            return true;
        }

        List<String> branches = new ArrayList<>();
        collectBranches(definition.getArgumentTreeBranches(), "", sender, false, branches);

        return !branches.isEmpty() || definition.getChildren().stream().anyMatch(child -> reachable(child, sender));
    }

    private static @NotNull String arguments(@NotNull BukkitCommandDefinition definition) {
        StringBuilder builder = new StringBuilder();

        for (BukkitArgument<?, ?> arg : definition.getRequiredArguments()) {
            builder.append(' ').append(token(arg));
        }

        for (BukkitArgument<?, ?> arg : definition.getOptionalArguments()) {
            builder.append(" [").append(arg.getName()).append(']');
        }

        return builder.toString();
    }

    private static void collectBranches(@NotNull List<? extends AbstractTreeArgument<?, ?, BukkitCommandDefinition, CommandSender, BukkitCommandExecutor<? extends CommandSender>, BukkitArgument<?, ?>, BukkitPermission, ?>> nodes,
                                        @NotNull String prefix,
                                        @NotNull CommandSender sender,
                                        boolean ignoreRequirements,
                                        @NotNull List<String> usages) {
        for (var node : nodes) {
            if (!ignoreRequirements && !node.getRequirement().test(sender)) {
                continue;
            }

            String path = prefix + " " + token(node);

            if (ignoreRequirements || node.getExecutors().stream().anyMatch(executor -> executor.getSourceType().isInstance(sender))) {
                usages.add(path);
            }

            collectBranches(node.getArguments(), path, sender, ignoreRequirements, usages);
        }
    }

    private static @NotNull String token(@NotNull AbstractArgument<?, ?, ?, ?> argument) {
        return argument instanceof LiteralArgument ? argument.getName() : "<" + argument.getName() + ">";
    }

    public static class HelpValue {

        private final String usage;
        private final String description;

        private HelpValue(@NotNull String usage, @Nullable String description) {
            this.usage = usage;
            this.description = description == null ? "" : description;
        }

        public String getUsage() {
            return usage;
        }

        public String getDescription() {
            return description;
        }

    }

}