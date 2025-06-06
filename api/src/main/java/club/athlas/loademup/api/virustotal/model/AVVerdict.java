package club.athlas.loademup.api.virustotal.model;

import org.jetbrains.annotations.NotNull;

public enum AVVerdict {

    UNDETECTED(false),
    MALICIOUS(true),
    SUSPICIOUS(true),
    HARMLESS(false),
    TYPE_UNSUPPORTED(false),
    TIMEOUT(false),
    FAILURE(false),
    CONFIRMED_TIMEOUT(false),
    EXCLUDED(false),
    UNKNOWN(false);

    private final boolean detected;

    AVVerdict(boolean detected) {
        this.detected = detected;
    }

    public boolean hasDetected() {
        return detected;
    }

    public static AVVerdict fromString(@NotNull String value) {
        try {
            return AVVerdict.valueOf(value.toUpperCase().replace("-", "_"));
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

}
