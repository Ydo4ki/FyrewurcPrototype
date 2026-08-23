package org.fw.lib.jlib.util;

import org.fw.core.base.Val;
import org.fw.core.base.context.RtEnv;
import org.fw.core.state.obj.State;
import org.fw.core.vit.Vit;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.Map;

public final class JvmUtils {

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

    private static final MethodHandle vitEvalM;
    static {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            vitEvalM = lookup.findVirtual(Vit.class, "eval",
                    MethodType.methodType(Val.class, RtEnv.class, State.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static MethodHandle toMethodHandle(Vit vit) {
        return vitEvalM.bindTo(vit);
    }
}
