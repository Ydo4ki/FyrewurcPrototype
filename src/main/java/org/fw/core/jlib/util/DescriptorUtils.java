package org.fw.core.jlib.util;

import java.util.HashMap;
import java.util.Map;

public final class DescriptorUtils {

    private static final Map<Class<?>, String> PRIMITIVE_DESCRIPTORS = new HashMap<>();

    static {
        PRIMITIVE_DESCRIPTORS.put(void.class, "V");
        PRIMITIVE_DESCRIPTORS.put(boolean.class, "Z");
        PRIMITIVE_DESCRIPTORS.put(char.class, "C");
        PRIMITIVE_DESCRIPTORS.put(byte.class, "B");
        PRIMITIVE_DESCRIPTORS.put(short.class, "S");
        PRIMITIVE_DESCRIPTORS.put(int.class, "I");
        PRIMITIVE_DESCRIPTORS.put(float.class, "F");
        PRIMITIVE_DESCRIPTORS.put(long.class, "J");
        PRIMITIVE_DESCRIPTORS.put(double.class, "D");
    }

    public static String getDescriptor(Class<?> clazz) {
        String primitiveDesc = PRIMITIVE_DESCRIPTORS.get(clazz);
        if (primitiveDesc != null) {
            return primitiveDesc;
        }

        if (clazz.isArray()) {
            return clazz.getName().replace('.', '/');
        }

        return "L" + clazz.getName().replace('.', '/') + ";";
    }
}
