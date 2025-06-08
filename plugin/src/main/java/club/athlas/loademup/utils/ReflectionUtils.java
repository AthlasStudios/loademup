package club.athlas.loademup.utils;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

@SuppressWarnings("unused")
public class ReflectionUtils {

    @SuppressWarnings("unchecked")
    public static <T> T accessField(@NotNull Object object, String fieldName, TypeReference<T> typeRef) throws NoSuchFieldException, IllegalAccessException {

        Field field = object.getClass().getDeclaredField(fieldName);

        boolean accessible = isAccessible(object, field);
        if (!accessible) field.setAccessible(true);

        Object value = field.get(object);

        if (isAccessible(object, field) != accessible) field.setAccessible(accessible);

        return (T) value;
    }

    public static <T> T accessField(@NotNull Object object, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        return accessField(object, fieldName, new TypeReference<>() { });
    }

    @SuppressWarnings("unchecked")
    public static <T> T accessFieldFromType(@NotNull Class<?> type, @NotNull Object object, String fieldName, TypeReference<T> typeRef) throws NoSuchFieldException, IllegalAccessException {
        Field field = type.getDeclaredField(fieldName);

        boolean accessible = isAccessible(object, field);
        if (!accessible) field.setAccessible(true);

        Object value = field.get(object);

        if (isAccessible(object, field) != accessible) field.setAccessible(accessible);

        return (T) value;
    }

    public static <T> T accessFieldFromType(@NotNull Class<?> type, @NotNull Object object, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        return accessFieldFromType(type, object, fieldName, new TypeReference<>() { });
    }

    @SuppressWarnings("unchecked")
    public static <T> T callMethod(@NotNull Object object, @NotNull String methodName, @NotNull TypeReference<T> typeRef, Object... args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?>[] paramTypes = Arrays.stream(args)
                .map(arg -> arg != null ? arg.getClass() : Object.class)
                .toArray(Class<?>[]::new);

        Method method = object.getClass().getDeclaredMethod(methodName, paramTypes);

        boolean accessible = isAccessible(object, method);
        if (!accessible) method.setAccessible(true);

        Object result = method.invoke(object, args);

        if (isAccessible(object, method) != accessible) method.setAccessible(accessible);

        return (T) result;
    }

    public static <T> T callMethod(@NotNull Object object, @NotNull String methodName, Object... args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return callMethod(object, methodName, new TypeReference<>() {}, args);
    }

    public static boolean isAccessible(@NotNull Object object, @NotNull Field field) {
        return isPublic(field.getModifiers()) && isPublic(field.getDeclaringClass().getModifiers());
    }

    public static boolean isAccessible(@NotNull Object object, @NotNull Method method) {
        return isPublic(method.getModifiers()) && isPublic(method.getDeclaringClass().getModifiers());
    }

    private static boolean isPublic(int modifiers) {
        return Modifier.isPublic(modifiers);
    }

}
