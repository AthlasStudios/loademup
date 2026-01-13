package club.athlas.loademup.server.commands.base.argument;

import org.jetbrains.annotations.Nullable;

public record CommandFlag(String name, @Nullable String description) {

    public CommandFlag(String name, @Nullable String description) {
        this.name = name;
        this.description = description;
    }

    public CommandFlag(String name) {
        this(name, null);
    }

}
