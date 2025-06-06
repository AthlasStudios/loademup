package club.athlas.loademup.utils;

import club.athlas.loademup.api.object.PluginData;
import me.gurwi.commandframework.utility.helpers.HelpValue;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.TagPattern;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class TagResolverUtils {

    @Contract("_ -> new")
    public static @NotNull TagResolver resolverFrom(PluginData data) {
        return TagResolver.resolver("plugin", (args, ctx) -> {
            final String param = args.popOr("param expected").value();
            String value = switch (param) {
                  case "name" -> data.name();
                  case "description" -> data.description();
                  case "author" -> data.authors().toString();
                  case "version" -> data.version();
                  default -> "<red>invalid param";
            };

            if (value == null) value = "null";
            return Tag.preProcessParsed(value);
        });
    }

    @Contract("_ -> new")
    public static @NotNull TagResolver resolverFrom(HelpValue helpValue) {
        return TagResolver.resolver("helpvalue", (args, ctx) -> {
            final String param = args.popOr("param expected").value();
            String value = switch (param) {
                case "command" -> helpValue.getCommand();
                case "description" -> helpValue.getDescription();
                case "permission" -> helpValue.getPermission().getPermission();
                default -> "<red>invalid param";
            };

            return Tag.preProcessParsed(value);
        });
    }

    @Contract("_ -> new")
    public static @NotNull TagResolver resolverFromTime(long millis) {
        return TagResolver.resolver("time", (args, ctx) -> {
            final String unit = args.popOr("time unit expected").value().toLowerCase();

            long value;
            String result;

            switch (unit) {
                case "millis" -> {
                    value = millis;
                    result = Long.toString(value);
                }
                case "seconds" -> {
                    value = TimeUnit.MILLISECONDS.toSeconds(millis);
                    result = value + "s";
                }
                case "minutes" -> {
                    value = TimeUnit.MILLISECONDS.toMinutes(millis);
                    result = value + "m";
                }
                case "hours" -> {
                    value = TimeUnit.MILLISECONDS.toHours(millis);
                    result = value + "h";
                }
                case "days" -> {
                    value = TimeUnit.MILLISECONDS.toDays(millis);
                    result = value + "d";
                }
                case "compact" -> {
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
                    long minutes = seconds / 60;
                    long hours = minutes / 60;
                    long days = hours / 24;

                    seconds %= 60;
                    minutes %= 60;
                    hours %= 24;

                    StringBuilder sb = new StringBuilder();
                    if (days > 0) sb.append(days).append("d ");
                    if (hours > 0) sb.append(hours).append("h ");
                    if (minutes > 0) sb.append(minutes).append("m ");
                    if (seconds > 0 || sb.isEmpty()) sb.append(seconds).append("s");

                    result = sb.toString().trim();
                }
                default -> result = "<red>invalid unit";
            }

            return Tag.preProcessParsed(result);
        });
    }

    public static @NotNull TagResolver listPlaceholder(@TagPattern String tag, Collection<String> collection) {
        return Placeholder.parsed(tag, String.join(", ", collection));
    }

}
