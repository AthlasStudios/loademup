package club.athlas.loademup.api.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class FileUtils {

    @Contract(pure = true)
    public static boolean isJarFile(@NotNull File file) {
        return file.getName().endsWith(".jar");
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
