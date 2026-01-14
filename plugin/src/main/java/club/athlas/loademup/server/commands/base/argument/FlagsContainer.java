package club.athlas.loademup.server.commands.base.argument;

import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FlagsContainer {

    public static final FlagsContainer EMPTY = new FlagsContainer(null, Collections.emptySet());

    private final Set<String> activeFlags = new HashSet<>();
    private final Map<String, String> flagValues = new HashMap<>();

    FlagsContainer(String flagsString, Set<CommandFlag> availableFlags) {

        if (flagsString == null || flagsString.trim().isEmpty()) {
            return;
        }

        String[] parts = flagsString.split("\\s+");
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];

            if (!part.startsWith("--")) continue;

            String flagName = part.substring(2);

            if (activeFlags.contains(flagName)) continue;
            activeFlags.add(flagName);

            if (i + 1 < parts.length && !parts[i + 1].startsWith("--") && !parts[i + 1].startsWith("-")) {
                Optional<CommandFlag> flagDef = availableFlags.stream()
                        .filter(f -> f.name().equals(flagName))
                        .findFirst();

                if (flagDef.isPresent()) {
                    flagValues.put(flagName, parts[i + 1]);
                    i++;
                }
            }

        }

    }

    public boolean get(String flagName) {
        return activeFlags.contains(flagName);
    }

    public boolean has(String flagName) {
        return activeFlags.contains(flagName);
    }

    public @Nullable String getValue(String flagName) {
        return flagValues.get(flagName);
    }

    public String getValue(String flagName, String defaultValue) {
        return flagValues.getOrDefault(flagName, defaultValue);
    }

    public Set<String> getActiveFlags() {
        return Collections.unmodifiableSet(activeFlags);
    }

    public boolean isEmpty() {
        return activeFlags.isEmpty();
    }

    public int size() {
        return activeFlags.size();
    }

    @Override
    public String toString() {
        return "FlagContainer{" +
                "activeFlags=" + activeFlags +
                ", flagValues=" + flagValues +
                '}';
    }

}
