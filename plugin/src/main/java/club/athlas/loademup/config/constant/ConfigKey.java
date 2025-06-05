package club.athlas.loademup.config.constant;

public enum ConfigKey {


    DEBUG("generic.debug"),
    LANG_FILE("generic.lang");

    private final String path;

    ConfigKey(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
