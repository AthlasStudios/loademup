package club.athlas.loademup.config.constant;

public enum LangKey {

    ;

    private final String configKey;

    LangKey(String configKey) {
        this.configKey = configKey;
    }

    public String getPath() {
        return configKey;
    }

}
