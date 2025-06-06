package club.athlas.loademup.utils;

import club.athlas.loademup.integration.ModrinthUpdateChecker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

public class BasicFunctions {

    public static @NotNull Component getUpdateCheckerComponent(@NotNull ModrinthUpdateChecker updateChecker) {
        if (updateChecker.getLatestCachedVersion() == null) {
            return Component.text("Build " + updateChecker.getCurrentVersion() + " (Couldn't check for updates) ✘", NamedTextColor.DARK_RED, TextDecoration.BOLD);
        }

        if (!updateChecker.getCurrentVersion().equals(updateChecker.getLatestCachedVersion())) {
            return Component.text("Build " + updateChecker.getCurrentVersion() + " (UpToDate) ", NamedTextColor.YELLOW)
                    .append(Component.text("✘", NamedTextColor.YELLOW, TextDecoration.BOLD));
        }

        return Component.text("Build " + updateChecker.getCurrentVersion() + " (Latest) ", NamedTextColor.GREEN)
                .append(Component.text("✔", NamedTextColor.GREEN, TextDecoration.BOLD));
    }

}
