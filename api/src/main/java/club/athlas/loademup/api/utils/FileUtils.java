package club.athlas.loademup.api.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

public class FileUtils {

    @Contract(pure = true)
    public static boolean isJarFile(@NotNull File file) {
        return file.getName().toLowerCase().endsWith(".jar");
    }

    public static boolean validateJarIntegrity(File pluginFile) {
        try (JarFile jarFile = new JarFile(pluginFile)) {
            jarFile.entries();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static @NotNull File createFile(String path, boolean replace) {
        File file = new File(path);
        if (file.exists() && !replace) return file;

        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return file;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static @NotNull File createFolder(String path) {
        File folder = new File(path);
        if (!folder.exists()) folder.mkdir();

        return folder;
    }

    public static boolean fileExists(String path) {
        return new File(path).exists();
    }

}
