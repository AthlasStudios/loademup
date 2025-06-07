package club.athlas.loademup.config.constant;

public enum LangKey {

    PREFIX("prefix"),

    PLUGIN_NOT_FOUND("pluginNotFound"),
    JAR_NOT_FOUND("jarNotFound"),
    INVALID_PLUGIN_JAR("invalidPluginJar"),
    UNSUPPORTED_PLUGIN_ACTION("unsupportedPluginAction"),

    MISSING_PLUGIN_DESCRIPTION("missingPluginDescription"),
    MISSING_PLUGIN_NAME("missingPluginName"),
    MISSING_MAIN_CLASS("missingMainClass"),
    MISSING_PLUGIN_VERSION("missingPluginVersion"),
    MISSING_DEPENDENCIES("missingDependencies"),
    MISSING_NEW_JAR_FILE("missingNewJarFile"),

    NO_MAIN_CLASS("noMainClass"),
    INVALID_MAIN_CLASS("invalidMainClass"),
    UNEXPECTED_ERROR("unexpectedError"),

    LOOKING_FOR_NEW_JAR_FILE("lookingForNewJar"),
    LOOKING_FOR_PLUGIN_YML("lookingForPluginYML"),
    INTEGRITY_CHECK_START("startingIntegrityCheck"),
    CHECKING_DEPENDENCIES("checkingDependencies"),
    CHECKING_DEPENDENTS("checkingDependants"),

    PLUGIN_LOAD_START("tryingToLoad"),
    PLUGIN_LOADED("pluginLoaded"),
    PLUGIN_LOAD_FAIL("pluginLoadFail"),
    PLUGIN_LOADED_BUT_DISABLED("pluginLoadedButDisabled"),
    PLUGIN_ALREADY_LOADED("pluginAlreadyLoaded"),

    PLUGIN_UNLOAD_START("tryingToUnload"),
    PLUGIN_UNLOAD_BLOCKED("pluginUnloadBlocked"),
    PLUGIN_UNLOADED("pluginUnloaded"),
    PLUGIN_UNLOAD_FAIL("pluginUnloadFail"),

    PLUGIN_RELOAD_START("tryingToReload"),
    PLUGIN_RELOAD_BLOCKED("pluginReloadBlocked"),
    PLUGIN_RELOADED("pluginReloaded"),
    PLUGIN_RELOAD_FAIL("pluginReloadFail"),
    RELOADING_DEPENDANDTS("reloadingDependants"),
    PLUGIN_DEPENDANT_RELOADED("pluginDependantReloaded"),
    PLUGIN_DEPENDANT_RELOAD_FAIL("pluginDependantReloadFail"),
    PLUGIN_DEPENDANTS_RELOAD_FINISH("pluginDependantsReloaded"),
    DEPENDAND_RELOAD_UNSUPPORTED("dependantReloadUnsupported"),
    DEPENDAND_CURRENT_JAR_NOT_FOUND("dependantCurrentJarNotFound"),

    PLUGIN_ENABLE_START("tryingToEnable"),
    PLUGIN_ENABLED("pluginEnabled"),
    PLUGIN_ENABLE_FAIL("pluginEnableFail"),
    PLUGIN_ALREADY_ENABLED("pluginAlreadyEnabled"),

    PLUGIN_DISABLE_START("tryingToDisable"),
    PLUGIN_DISABLED("pluginDisabled"),
    PLUGIN_DISABLE_FAIL("pluginDisableFail"),
    PLUGIN_ALREADY_DISABLED("pluginAlreadyDisabled"),

    VIRUS_SCAN_START("virusScanStart"),
    VIRUS_SCAN_CLEAR("virusScanClear"),
    VIRUS_SCAN_THREATS_FOUND("virusScanThreatsFound"),
    VIRUS_SCAN_RESULT_FORMAT("virusScanResultFormat"),

    VIRUS_SCAN_UNABLE_TO_SCAN("virusScanUnableToScan"),
    VIRUS_SCAN_NO_RESULT("virusScanNoResult"),
    VIRUS_SCAN_SERVICE_DISABLED("virusScanServiceDisabled"),

    HELP_MESSAGE_HEADER("help.header"),
    HELP_MESSAGE_COMMAND_FORMAT("help.commandFormat"),
    HELP_MESSAGE_FOOTER("help.footer");

    private final String configKey;

    LangKey(String configKey) {
        this.configKey = configKey;
    }

    public String getPath() {
        return configKey;
    }

}
