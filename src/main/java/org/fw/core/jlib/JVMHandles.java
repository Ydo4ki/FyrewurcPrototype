package org.fw.core.jlib;

import org.fw.core.FW;
import org.fw.core.base.BoolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.jlib.data.*;
import org.fw.core.lib.*;
import org.fw.core.lib.state.SystemOperation;
import org.fw.core.state.operation.Operation;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static org.fw.core.FW.symbol;

public final class JVMHandles {
    public static final MethodHandles.Lookup lookup = MethodHandles.lookup();

    public static final ClassLoader fwClassLoader = new JFWClassLoader(Thread.currentThread().getContextClassLoader());

    public static final Val jvmEnv = ModuleFw.module(
            DeclaredFw.declared(symbol("get-type"), FW.telephonist((arg) -> {
                if (!arg.type().equals(StrFw.str))
                    return null;
                String descriptor = arg._unpack();
                return new SystemOperation() {
                    @Override
                    protected Val execute0() {
                        Class<?> cls;
                        cls = MethodType.fromMethodDescriptorString("()" + descriptor, fwClassLoader).returnType();
                        return Val.of(JClassFw.jClass, cls);
                    }
                }.asVal();
            }))
    );

    static Val jwrap(Object jObj, Class<?> aClass) {
        if (aClass.isInstance(jObj)) return Val.of(JOopFw.jOop, jObj);
        if (aClass == boolean.class && jObj instanceof Boolean) return BoolFw.wrap((Boolean) jObj);
        if (aClass == byte.class && jObj instanceof Byte) return JByteFw.wrap((Byte) jObj);
        if (aClass == char.class && jObj instanceof Character) return JCharFw.wrap((Character) jObj);
        if (aClass == short.class && jObj instanceof Short) return JShortFw.wrap((Short) jObj);
        if (aClass == float.class && jObj instanceof Float) return JFloatFw.wrap((Float) jObj);
        if (aClass == int.class && jObj instanceof Integer) return JIntFw.wrap((Integer) jObj);
        if (aClass == double.class && jObj instanceof Double) return JDoubleFw.wrap((Double) jObj);
        if (aClass == long.class && jObj instanceof Long) return JLongFw.wrap((Long) jObj);
        if (aClass == void.class) return Operation.unit;
        throw new IllegalArgumentException(jObj.toString() + " | " + aClass.getCanonicalName());
    }

    public static Object junwrap(Val val) {
        Type type = val.type();
        if (type == JOopFw.jOop) return val._unpack();
        if (type == BoolFw.bool) return val._unpack(Boolean.class);
        if (type == JByteFw.jbyte) return JByteFw.unwrap(val);
        if (type == JCharFw.jchar) return JCharFw.unwrap(val);
        if (type == JShortFw.jshort) return JShortFw.unwrap(val);
        if (type == JFloatFw.jfloat) return JFloatFw.unwrap(val);
        if (type == JIntFw.jint) return JIntFw.unwrap(val);
        if (type == JDoubleFw.jdouble) return JDoubleFw.unwrap(val);
        if (type == JLongFw.jlong) return JLongFw.unwrap(val);
        throw new IllegalArgumentException(val.toString());
    }
}
