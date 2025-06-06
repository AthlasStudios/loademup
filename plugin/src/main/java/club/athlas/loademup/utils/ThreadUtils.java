package club.athlas.loademup.utils;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class ThreadUtils {

    private static final Set<String> KNOWN_THREAD_PREFIXES = Set.of(
            "Craft", "Netty", "Paper", "Async Chat", "Server thread", "ForkJoinPool"
    );

    public static @NotNull Set<Thread> getAllThreads() {
        return Thread.getAllStackTraces().keySet();
    }

    public static boolean isThreadFromPlugin(@NotNull Thread thread, @NotNull Plugin plugin) {
        ClassLoader pluginClassLoader = plugin.getClass().getClassLoader();
        if (pluginClassLoader.equals(thread.getClass().getClassLoader())) return true;

        Runnable target = getRunnableTarget(thread);
        return target != null && pluginClassLoader.equals(target.getClass().getClassLoader());
    }

    public static @NotNull Set<Thread> getThreadsFromPlugin(Plugin plugin) {
        Set<Thread> result = new HashSet<>();
        for (Thread thread : getAllThreads()) {
            if (isThreadFromPlugin(thread, plugin) && !isSystemThread(thread)) {
                result.add(thread);
            }
        }
        return result;
    }

    private static @Nullable Runnable getRunnableTarget(Thread thread) {
        try {
            Field targetField = Thread.class.getDeclaredField("target");
            targetField.setAccessible(true);
            Object target = targetField.get(thread);
            if (target instanceof Runnable) return (Runnable) target;
        } catch (Throwable ignored) {}
        return null;
    }

    private static boolean isSystemThread(Thread thread) {
        for (String prefix : KNOWN_THREAD_PREFIXES) {
            if (thread.getName().startsWith(prefix)) return true;
        }
        return false;
    }

}
