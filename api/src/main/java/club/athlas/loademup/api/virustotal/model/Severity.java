package club.athlas.loademup.api.virustotal.model;

public enum Severity {

    LOW,
    MEDIUM,
    HIGH;

    public static Severity inferSeverity(AVVerdict verdict, String type) {
        if (verdict == AVVerdict.SUSPICIOUS) return MEDIUM;
        if (verdict == AVVerdict.MALICIOUS) if (type == null) return HIGH;
        return LOW;
    }

}
