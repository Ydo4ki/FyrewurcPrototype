package org.fw.core.jlib;

import org.fw.core.FW;
import org.fw.core.base.BoolFw;
import org.fw.core.base.Val;
import org.fw.core.jlib.data.JOopFw;
import org.fw.core.lib.*;
import org.fw.core.lib.state.SystemOperation;
import org.fw.core.state.operation.Operation;

import java.lang.invoke.MethodHandles;

import static org.fw.core.FW.symbol;

public final class JVMHandles {
    static final MethodHandles.Lookup lookup = MethodHandles.lookup();

    private static final ClassLoader fwClassLoader = new JFWClassLoader(Thread.currentThread().getContextClassLoader());

    public static final Val jvmEnv = ModuleFw.module(
            DeclaredFw.declared(symbol("get-class"), FW.telephonist((arg) -> {
                if (!arg.type().equals(StrFw.str))
                    return null;
                String name = arg._unpack();
                return new SystemOperation() {
                    @Override
                    protected Val execute0() {
                        Class<?> cls;
                        try {
                            cls = fwClassLoader.loadClass(name);
                        } catch (ClassNotFoundException e) {
                            return Operation.unit;
                        }
                        return Val.of(JClassFw.jClass, cls);
                    }
                }.asVal();
            }))
    );

    static Val wrap(Object jObj, Class<?> aClass) {
        if (aClass.isInstance(jObj)) return Val.of(JOopFw.jOop, jObj);
        if (aClass == boolean.class && jObj instanceof Boolean) return BoolFw.wrap((Boolean) jObj);
        if (aClass == byte.class && jObj instanceof Byte) return null;
        if (aClass == char.class && jObj instanceof Character) return null;
        if (aClass == short.class && jObj instanceof Short) return null;
        if (aClass == float.class && jObj instanceof Float) return null;
        if (aClass == int.class && jObj instanceof Integer) return null;
        if (aClass == double.class && jObj instanceof Double) return null;
        if (aClass == long.class && jObj instanceof Long) return null;
        if (aClass == void.class) return Operation.unit;
        return null;
    }
}
