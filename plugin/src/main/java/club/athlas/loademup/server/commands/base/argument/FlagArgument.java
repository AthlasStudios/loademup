package club.athlas.loademup.server.commands.base.argument;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.jorel.commandapi.IStringTooltip;
import dev.jorel.commandapi.StringTooltip;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandArguments;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FlagArgument extends SafeOverrideableArgument<FlagsContainer, String> implements GreedyArgument {

    private final Set<CommandFlag> flags = new HashSet<>();

    public FlagArgument(String nodeName) {
        super(nodeName, StringArgumentType.greedyString(), s -> s);

        replaceSuggestions(
                ArgumentSuggestions.stringsWithTooltips(info -> {
                    String currentInput = info.currentArg();
                    if (currentInput == null) currentInput = "";

                    String[] parts = currentInput.split("\\s+");
                    String lastPart = parts.length > 0 ? parts[parts.length - 1] : "";

                    Set<String> usedFlags = new HashSet<>();
                    for (String part : parts) {
                        if (part.startsWith("--")) {
                            usedFlags.add(part.substring(2));
                        }
                    }

                    String prefix = currentInput.substring(0, currentInput.length() - lastPart.length());

                    return flags.stream()
                            .filter(flag -> !usedFlags.contains(flag.name()))
                            .map(flag -> StringTooltip.ofString(
                                    prefix + "--" + flag.name(),
                                    flag.description()
                            ))
                            .toArray(IStringTooltip[]::new);
                })
        );
    }

    @Override
    public Class<FlagsContainer> getPrimitiveType() {
        return FlagsContainer.class;
    }

    @Override
    public CommandAPIArgumentType getArgumentType() {
        return CommandAPIArgumentType.PRIMITIVE_GREEDY_STRING;
    }

    @Override
    public <Source> FlagsContainer parseArgument(@NotNull CommandContext<Source> cmdCtx, String key, CommandArguments previousArgs) {
        try {
            String flagsString = cmdCtx.getArgument(key, String.class);
            FlagsContainer container = new FlagsContainer(flagsString, flags);
            return container;
        } catch (Exception e) {
            return new FlagsContainer("", flags);
        }
    }

    public FlagArgument withFlags(CommandFlag... flags) {
        this.flags.addAll(Set.of(flags));
        return this;
    }

    public static boolean getFlag(CommandArguments args, String flagName) {
        try {
            Boolean value = (Boolean) args.get(flagName);
            return value != null && value;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean hasFlag(CommandArguments args, String flagArgumentName, String flagName) {
        try {
            String flagsContainer = args.getRaw(flagArgumentName);
            if (flagsContainer == null || flagsContainer.isEmpty()) {
                return false;
            }
            return flagsContainer.contains("--" + flagName);
        } catch (Exception e) {
            return false;
        }
    }

}