package club.athlas.loademup.config.constant;

public enum ConfigKey {


    DEBUG("generic.debug"),
    LANG_FILE("generic.lang"),

    VIRUS_SCANNER_ENABLED("virus-scanner.enabled"),
    VIRUS_SCANNER_RUN_PRELOAD("virus-scanner.run-preload"),
    VIRUS_SCANNER_API_KEY("virus-scanner.vt-key");

    private final String path;

    ConfigKey(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
