package club.athlas.loademup.api.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class FileUtils {

    @Contract(pure = true)
    public static boolean isJarFile(@NotNull File file) {
        return file.getName().endsWith(".jar");
    }

}
