package club.athlas.loademup.api.result;

public enum ClassLoaderValidationResult {

    VALID,
    MISSING_PLUGIN_DESCRIPTION,
    NO_MAIN_CLASS,
    FILE_NOT_FOUND,
    INVALID_MAIN_CLASS,
    UNEXPECTED_ERROR

}
