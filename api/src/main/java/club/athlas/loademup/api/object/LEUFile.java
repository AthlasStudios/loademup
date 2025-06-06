package club.athlas.loademup.api.object;

import club.athlas.loademup.api.constant.LEUAction;
import club.athlas.loademup.api.utils.FileUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class LEUFile {

    private final String version;
    private final boolean supported;
    private final Set<LEUAction> supportedActions;

    public LEUFile(String version, boolean supported, Set<LEUAction> supportedActions) {
        this.version = version;
        this.supported = supported;
        this.supportedActions = supportedActions;
    }

    public LEUFile(InputStream stream) throws IOException {
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(stream);

        if (data == null) {
            throw new IOException("YAML parsing failed or file was empty");
        }

        Object version = data.get("version");
        if (!(version instanceof String)) {
            throw new IOException("'version' must be a string");
        }

        this.version = (String) version;

        Object supportedValue = data.get("supported");
        if (!(supportedValue instanceof Boolean)) {
            throw new IOException("'supported' must be a boolean");
        }

        this.supported = (Boolean) supportedValue;

        Object actions = data.get("supported-actions");
        if (actions != null) {
            if (!(actions instanceof List)) {
                throw new IOException("'supported-actions' must be a list");
            }

            try {
                this.supportedActions = ((List<?>) actions).stream()
                        .map(Object::toString)
                        .map(String::toUpperCase)
                        .map(LEUAction::valueOf)
                        .collect(Collectors.toSet());
            } catch (IllegalArgumentException e) {
                throw new IOException("Invalid action in 'supported-actions': " + e.getMessage(), e);
            }

            return;
        }

        this.supportedActions = new HashSet<>();
    }

    public static Optional<LEUFile> from(File file) {
        if (!FileUtils.isJarFile(file)) return Optional.empty();

        try (JarFile jar = new JarFile(file)) {
            JarEntry entry = jar.getJarEntry("loademup.yml");
            if (entry == null) return Optional.empty();

            try (InputStream is = jar.getInputStream(entry)) {
                return Optional.of(new LEUFile(is));
            }
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public boolean isSupported(LEUAction action) {
        if (!isSupported()) return false;
        return supportedActions.contains(action) || supportedActions.contains(LEUAction.ALL);
    }

    public String getVersion() {
        return version;
    }

    public boolean isSupported() {
        return supported || supportedActions.contains(LEUAction.NONE);
    }

    public Set<LEUAction> getSupportedActions() {
        return supportedActions;
    }

}
