package club.athlas.loademup.api.constant;

public interface LEUPermission {

    String FULL_PERM = "loademup.*";
    String COMMAND_PREFIX = "loademup.command.";

    String LOAD_COMMAND = COMMAND_PREFIX + "load";
    String UNLOAD_COMMAND = COMMAND_PREFIX + "unload";
    String RELOAD_COMMAND = COMMAND_PREFIX + "reload";
    String ENABLE_COMMAND = COMMAND_PREFIX + "enable";
    String DISABLE_COMMAND = COMMAND_PREFIX + "disable";
    String SCAN_COMMAND = COMMAND_PREFIX + "scan";
    String INFO_COMMAND = COMMAND_PREFIX + "info";

}
