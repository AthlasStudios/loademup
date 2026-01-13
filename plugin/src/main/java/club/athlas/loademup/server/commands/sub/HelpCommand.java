package club.athlas.loademup.server.commands.sub;

import club.athlas.loademup.LoadEmUp;
import club.athlas.loademup.api.constant.LEUPermission;
import club.athlas.loademup.config.Messages;
import club.athlas.loademup.config.constant.LangKey;
import club.athlas.loademup.server.commands.base.PluginCommand;
import club.athlas.loademup.utils.BasicFunctions;
import club.athlas.loademup.utils.TagResolverUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.function.Function;

public class HelpCommand extends PluginCommand<LoadEmUp> {

    private final Messages messages;

    public  HelpCommand(LoadEmUp plugin) {
        super(plugin, "help");

        this.messages = plugin.getMessages();
    }

    @Override
    public void setup() {

        executes((sender, args) -> {
            Component helpMessage = getHelpMessage(sender);
            plugin.getAdventure().sender(sender).sendMessage(helpMessage);
        });

    }

    @Contract(" -> new")
    private @NotNull @Unmodifiable List<HelpValue> getHelpValues() {
        return List.of(
                new HelpValue(
                        "loademup help",
                        "View this message",
                        sender -> sender.getEffectivePermissions().stream()
                                .anyMatch(p -> p.getPermission().startsWith(LEUPermission.COMMAND_PREFIX))
                ),
                new HelpValue(
                        "loademup load <plugin>",
                        "Load a new plugin.",
                        sender -> sender.hasPermission(LEUPermission.LOAD_COMMAND)
                ),
                new HelpValue(
                        "loademup unload <plugin> [flags]",
                        "Unload a plugin.",
                        sender -> sender.hasPermission(LEUPermission.UNLOAD_COMMAND)
                ),
                new HelpValue(
                        "loademup reload <plugin> [flags]",
                        "Reload a plugin.",
                        sender -> sender.hasPermission(LEUPermission.RELOAD_COMMAND)
                ),
                new HelpValue(
                        "loademup enable <plugin>",
                        "Enable a plugin",
                        sender -> sender.hasPermission(LEUPermission.ENABLE_COMMAND)
                ),
                new HelpValue(
                        "loademup disable <plugin>",
                        "Disable a plugin",
                        sender -> sender.hasPermission(LEUPermission.DISABLE_COMMAND)
                ),
                new HelpValue(
                        "loademup scan <plugin>",
                        "Scan a plugin",
                        sender -> sender.hasPermission(LEUPermission.SCAN_COMMAND)
                ),
                new HelpValue(
                        "loademup info <plugin>",
                        "Get a plugin info.",
                        sender -> sender.hasPermission(LEUPermission.INFO_COMMAND)
                )
        );
    }

    private @NotNull Component getHelpMessage(CommandSender sender) {
        TagResolver.Single[] commonPlaceholders = {
                Placeholder.component("build", BasicFunctions.getUpdateCheckerComponent(plugin.getUpdateChecker())),
                Placeholder.parsed("version", plugin.getDescription().getVersion()),
                Placeholder.parsed("latest", plugin.getUpdateChecker().getLatestCachedVersion())
        };

        TextComponent.Builder builder = Component.text();

        if (!messages.isEmpty(LangKey.HELP_MESSAGE_HEADER)) builder.append(messages.getComponent(LangKey.HELP_MESSAGE_HEADER, commonPlaceholders));


        if (!messages.isEmpty(LangKey.HELP_MESSAGE_COMMAND_FORMAT)) {
                List<HelpValue> visibleHelpValues = getHelpValues().stream()
                        .filter(helpValue -> helpValue.getRequirement().apply(sender) || sender.hasPermission(LEUPermission.FULL_PERM))
                        .toList();

                for (int i = 0; i < visibleHelpValues.size(); i++) {
                    HelpValue helpValue = visibleHelpValues.get(i);
                    builder.append(messages.getComponent(LangKey.HELP_MESSAGE_COMMAND_FORMAT, TagResolverUtils.resolverFrom(helpValue)));
                    if (i < visibleHelpValues.size() - 1) builder.appendNewline();
                }
            }

        if (!messages.isEmpty(LangKey.HELP_MESSAGE_FOOTER)) builder.append(messages.getComponent(LangKey.HELP_MESSAGE_FOOTER, commonPlaceholders));

        return builder.build();
    }

    public static class HelpValue {

        private final String usage;
        private final String description;
        private final Function<CommandSender, Boolean> requirement;

        private HelpValue(String usage, String description, Function<CommandSender, Boolean> requirement) {
            this.usage = usage;
            this.description = description;
            this.requirement = requirement;
        }

        public String getUsage() {
            return usage;
        }

        public String getDescription() {
            return description;
        }

        public Function<CommandSender, Boolean> getRequirement() {
            return requirement;
        }

    }

}
