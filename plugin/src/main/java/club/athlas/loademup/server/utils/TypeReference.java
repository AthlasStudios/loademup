package club.athlas.loademup.server.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@SuppressWarnings("unused")
public class TypeReference<T> {

    private final Type type;

    public TypeReference() {
        Type superClass = getClass().getGenericSuperclass();

        if (superClass instanceof Class<?>) {
            throw new RuntimeException("TypeReference must be created as an anonymous subclass, e.g., new TypeReference<List<String>>() {}");
        }

        this.type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    public Type getType() {
        return type;
    }

}
