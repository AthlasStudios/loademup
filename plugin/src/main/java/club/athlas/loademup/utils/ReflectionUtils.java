package club.athlas.loademup.utils;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

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

    public static boolean isAccessible(@NotNull Object object, @NotNull Field field) {
        return Modifier.isPublic(field.getModifiers()) &&
                Modifier.isPublic(field.getDeclaringClass().getModifiers());
    }

}
