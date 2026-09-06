package com.ydo4ki.fw.internal.lib.jlib._internal;

import com.ydo4ki.fw.internal.lib.jlib.data.*;
import org.fw.core.FW;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.lib.stdlib.DeclaredFw;
import org.fw.lib.stdlib.ModuleFw;
import com.ydo4ki.fw.internal.lib.stdlib.StrFw;
import com.ydo4ki.fw.internal.lib.stdlib.state.SystemOperation;
import org.fw.core.state.operation.Operation;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;

import static org.fw.core.FW.symbol;

public final class JVMHandles {
    public static final MethodHandles.Lookup lookup = MethodHandles.publicLookup();

    public static final ClassLoader fwClassLoader = new JFWClassLoader(Thread.currentThread().getContextClassLoader());

    public static Class<?> findType(String descriptor) {
        return MethodType.fromMethodDescriptorString("()" + descriptor, fwClassLoader).returnType();
    }

    public static final Val jvmEnv = ModuleFw.module(
            // let's just assume find-X is an operation and get-X is pure
            // that would be more intuitive
            DeclaredFw.declared(symbol("str2jstring"), FW.telephonist_native((arg) -> {
                if (!arg.getType().equals(StrFw.str))
                    return null;
                String string = arg._UNPACK();
                return jwrap(string, String.class);
            })),
            DeclaredFw.declared(symbol("jstring2str"), FW.telephonist_native((arg) -> {
                if (!arg.getType().equals(JOopFw.jOop) || !(arg._UNPACK() instanceof String))
                    return null;
                String string = arg._UNPACK();
                return StrFw.str(string);
            })),
            DeclaredFw.declared(symbol("find-type"), FW.telephonist_native((arg) -> {
                if (!arg.getType().equals(StrFw.str))
                    return null;
                String descriptor = arg._UNPACK();
                return new SystemOperation() {
                    @Override
                    protected Val apply0() {
                        Class<?> cls = findType(descriptor);
                        return Val.of(JClassFw.jClass, cls);
                    }
                }.asVal();
            })),
            DeclaredFw.declared(symbol("find-array-constructor"), FW.telephonist_native((arg) -> {
                if (!arg.getType().equals(StrFw.str))
                    return null;
                String descriptor = arg._UNPACK();
                return new SystemOperation() {
                    @Override
                    protected Val apply0() {
                        Class<?> elementType = findType(descriptor);
                        MethodHandle arrayCtor;
                        try {
                            arrayCtor = MethodHandles.lookup().findStatic(
                                    Array.class,
                                    "newInstance",
                                    MethodType.methodType(Object.class, Class.class, int.class)
                            );
                        } catch (NoSuchMethodException | IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        return Val.of(JMethodFw.jMethod, arrayCtor.bindTo(elementType));
                    }
                }.asVal();
            })),
            DeclaredFw.declared(symbol("find-array-setter"), FW.telephonist_native((arg) -> {
                if (!arg.getType().equals(StrFw.str))
                    return null;
                String descriptor = arg._UNPACK();
                return new SystemOperation() {
                    @Override
                    protected Val apply0() {
                        Class<?> arrayClass = findType(descriptor);

                        return Val.of(JMethodFw.jMethod, MethodHandles.arrayElementSetter(arrayClass));
                    }
                }.asVal();
            })),
            DeclaredFw.declared(symbol("find-array-getter"), FW.telephonist_native((arg) -> {
                if (!arg.getType().equals(StrFw.str))
                    return null;
                String descriptor = arg._UNPACK();
                return new SystemOperation() {
                    @Override
                    protected Val apply0() {
                        Class<?> arrayClass = findType(descriptor);

                        return Val.of(JMethodFw.jMethod, MethodHandles.arrayElementGetter(arrayClass));
                    }
                }.asVal();
            }))
    );

    static Val jwrap(Object jObj, Class<?> aClass) {
        if (aClass.isInstance(jObj)) return Val.of(JOopFw.jOop, jObj);
        if (aClass == boolean.class && jObj instanceof Boolean) return JBooleanFw.wrap((Boolean) jObj);
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
        Type type = val.getType();
        if (type == JOopFw.jOop) return val._UNPACK();
        if (type == JBooleanFw.jboolean) return val._UNPACK(Boolean.class);
        if (type == JByteFw.jbyte) return JByteFw.unwrap(val);
        if (type == JCharFw.jchar) return (char)(short)JCharFw.unwrap(val);
        if (type == JShortFw.jshort) return JShortFw.unwrap(val);
        if (type == JFloatFw.jfloat) return JFloatFw.unwrap(val);
        if (type == JIntFw.jint) return JIntFw.unwrap(val);
        if (type == JDoubleFw.jdouble) return JDoubleFw.unwrap(val);
        if (type == JLongFw.jlong) return JLongFw.unwrap(val);
        throw new IllegalArgumentException(val.toString());
    }
}
