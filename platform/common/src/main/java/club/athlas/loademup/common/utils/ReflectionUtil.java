package club.athlas.loademup.common.utils;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;
import java.util.Arrays;

public class ReflectionUtil {

    public static <T> @NotNull T instantiate(@NotNull Class<T> type, @NotNull Class<?>[] parameterTypes, Object... args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<T> constructor = type.getDeclaredConstructor(parameterTypes);
        boolean accessible = isAccessible(constructor);

        if (!accessible) {
            constructor.setAccessible(true);
        }

        T result = constructor.newInstance(args);

        if (!accessible) {
            constructor.setAccessible(false);
        }

        return result;
    }

    public static <T> @NotNull T instantiate(@NotNull Class<T> type, Object... args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?>[] parameterTypes = Arrays.stream(args)
                .map(arg -> arg != null ? arg.getClass() : Object.class)
                .toArray(Class<?>[]::new);

        return instantiate(type, parameterTypes, args);
    }

    @SuppressWarnings("unchecked")
    public static <T> T accessField(@NotNull Object object, String fieldName, TypeReference<T> typeRef) throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        boolean accessible = isAccessible(object, field);

        if (!accessible) {
            field.setAccessible(true);
        }

        Object value = field.get(object);

        if (isAccessible(object, field) != accessible) {
            field.setAccessible(accessible);
        }

        return (T) value;
    }

    public static <T> T accessField(@NotNull Object object, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        return accessField(
                object,
                fieldName,
                new TypeReference<>() { }
        );
    }

    @SuppressWarnings("unchecked")
    public static <T> T accessFieldFromType(@NotNull Class<?> type, @NotNull Object object, String fieldName, TypeReference<T> typeRef) throws NoSuchFieldException, IllegalAccessException {
        Field field = type.getDeclaredField(fieldName);
        boolean accessible = isAccessible(object, field);

        if (!accessible) {
            field.setAccessible(true);
        }

        Object value = field.get(object);

        if (isAccessible(object, field) != accessible) {
            field.setAccessible(accessible);
        }

        return (T) value;
    }

    public static <T> T accessFieldFromType(@NotNull Class<?> type, @NotNull Object object, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        return accessFieldFromType(type, object, fieldName, new TypeReference<>() { });
    }

    @SuppressWarnings("unchecked")
    public static <T> T callMethod(@NotNull Object object, @NotNull String methodName, @NotNull TypeReference<T> typeRef, Object... args) throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {

        Class<?>[] paramTypes = Arrays.stream(args)
                .map(arg -> arg != null ? arg.getClass() : Object.class)
                .toArray(Class<?>[]::new);

        Method method = object.getClass().getDeclaredMethod(
                methodName,
                paramTypes
        );

        boolean accessible = isAccessible(object, method);

        if (!accessible) {
            method.setAccessible(true);
        }

        Object result = method.invoke(object, args);

        if (isAccessible(object, method) != accessible) {
            method.setAccessible(accessible);
        }

        return (T) result;
    }

    public static <T> T callMethod(@NotNull Object object, @NotNull String methodName, Object... args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return callMethod(
                object,
                methodName,
                new TypeReference<>() { },
                args
        );
    }

    public static void setField(@NotNull Object instance, @NotNull String fieldName, @NotNull Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = instance.getClass().getDeclaredField(fieldName);
        boolean accessible = isAccessible(instance, field);

        if (!accessible) {
            field.setAccessible(true);
        }

        field.set(instance, value);

        if (isAccessible(instance, field) != accessible) {
            field.setAccessible(accessible);
        }
    }

    public static boolean isAccessible(@NotNull Object object, @NotNull Field field) {
        return isPublic(field.getModifiers()) && isPublic(field.getDeclaringClass().getModifiers());
    }

    public static boolean isAccessible(@NotNull Object object, @NotNull Method method) {
        return isPublic(method.getModifiers()) && isPublic(method.getDeclaringClass().getModifiers());
    }

    public static boolean isAccessible(@NotNull Constructor<?> constructor) {
        return isPublic(constructor.getModifiers()) && isPublic(constructor.getDeclaringClass().getModifiers());
    }

    private static boolean isPublic(int modifiers) {
        return Modifier.isPublic(modifiers);
    }

}