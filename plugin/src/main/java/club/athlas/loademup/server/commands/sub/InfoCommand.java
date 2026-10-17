package club.athlas.loademup.server.commands.sub;

import club.athlas.loademup.LoadEmUp;
import club.athlas.loademup.api.constant.LEUPermission;
import club.athlas.loademup.api.loader.PluginLoader;
import club.athlas.loademup.api.object.Dependency;
import club.athlas.loademup.api.object.PluginData;
import club.athlas.loademup.server.commands.argument.PluginArgument;
import club.athlas.loademup.server.commands.base.PluginCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class InfoCommand extends PluginCommand<LoadEmUp> {

    private final PluginLoader loader;

    public InfoCommand(LoadEmUp plugin) {
        super(plugin, "info");

        this.loader = plugin.getLoader();
    }

    @Override
    public void setup() {
        withDescription("Display information about a plugin.");
        withPermissions(LEUPermission.INFO_COMMAND);

        withArguments(
                new PluginArgument(plugin, "plugin")
        );

        executes((sender, args) -> {
            Plugin target = args.get("plugin");

            PluginData data = PluginData.from(target);
            TextComponent.@NotNull Builder component = Component.text();

            component.appendNewline();
            component.append(
                    Component.text(" " + data.name(), NamedTextColor.YELLOW, TextDecoration.BOLD),
                    Component.text(" v" + data.version().replace("v", ""), NamedTextColor.GRAY),
                    Component.text(" - ", NamedTextColor.DARK_GRAY),
                    Component.text(target.isEnabled() ? "Enabled" : "Disabled", target.isEnabled() ? NamedTextColor.GREEN : NamedTextColor.RED, TextDecoration.BOLD)
            );

            component.appendNewline().appendNewline();

            if (!data.authors().isEmpty()) component.append(
                    Component.text("    Author(s) ", NamedTextColor.YELLOW), Component.text(data.authors().toString().replace("[", "").replace("]", "")),
                    Component.newline()
            );

            if (!data.contributors().isEmpty()) component.append(
                    Component.text("    Contributors ", NamedTextColor.YELLOW), Component.text(data.contributors().toString().replace("[", "").replace("]", "")),
                    Component.newline()
            );

            if (data.description() != null && !data.description().isEmpty()) component.append(
                    Component.text("    Description ", NamedTextColor.YELLOW), Component.text(data.description()),
                    Component.newline()
            );

            if (data.website() != null && !data.website().isEmpty()) component.append(
                    Component.text("    Description ", NamedTextColor.YELLOW), Component.text(data.website()),
                    Component.newline()
            );

            component.append(Component.text("    API-Ver ", NamedTextColor.YELLOW), Component.text(data.apiVersion()));
            component.appendNewline().appendNewline();

            List<Dependency> dependencies = loader.getDependencies(target);

            component.append(appendDependencyList("Dependencies", filterDeps(dependencies, Dependency::required)));
            component.appendNewline().appendNewline();
            component.append(appendDependencyList("Soft-Dependencies", filterDeps(dependencies, dependency -> !dependency.required())));

            List<Dependency> dependents = loader.getDependents(target);

            component.appendNewline().appendNewline();
            component.append(appendDependencyList("Dependants", filterDeps(dependents, Dependency::required)));
            component.appendNewline().appendNewline();

            component.append(appendDependencyList("Soft-Dependants", filterDeps(dependents, dependency -> !dependency.required())));
            component.appendNewline();

            plugin.getAdventure().sender(sender).sendMessage(component);

        });
    }

    private @NotNull TextComponent appendDependencyList(String title, @NotNull List<Dependency> dependencies) {
        if (dependencies.isEmpty()) return Component.text("    " + title, NamedTextColor.YELLOW)
                .append(Component.text(" None", NamedTextColor.RED));

        TextComponent.Builder component = Component.text();

        component.append(Component.text("    " + title, NamedTextColor.YELLOW));
        component.appendNewline();

        for (int i = 0; i < dependencies.size(); i++) {
            Dependency dep = dependencies.get(i);
            boolean enabled = dep.isLoaded();
            NamedTextColor color = enabled ? NamedTextColor.GREEN : NamedTextColor.RED;

            component.append(Component.text("      ⌊ ", NamedTextColor.DARK_GRAY, TextDecoration.BOLD));
            component.append(Component.text(dep.name(), color));
            if (i < dependencies.size() - 1) component.appendNewline();
        }

        return component.build();
    }

    private List<Dependency> filterDeps(@NotNull Collection<Dependency> dependencies, Predicate<Dependency> predicate) {
        return dependencies.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

}