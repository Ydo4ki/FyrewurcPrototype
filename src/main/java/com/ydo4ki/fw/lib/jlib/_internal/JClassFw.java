package com.ydo4ki.fw.lib.jlib._internal;

import org.fw.core.FW;
import org.fw.core.ast.Symbol;
import org.fw.core.base.*;
import com.ydo4ki.fw.lib.devicelib.PrimitiveLayoutsFw;
import com.ydo4ki.fw.lib.jlib.data.JOopFw;
import com.ydo4ki.fw.lib.jlib.util.JvmUtils;
import org.fw.lib.stdlib.StrFw;
import org.fw.core.state.operation.Operation;
import org.fw.core.util.FwUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public final class JClassFw {
    public static final Type jClass = FW.telephonist((arg) -> {
        if (FwUtils.isTypeApiCall(arg, JClassFw.jClass)) {
            Val instance = CallFw.getVal(arg);
            arg = CallFw.getArg(arg);
            if (arg.type() != SymbolFw.symbol)
                return null;

            Class<?> cls = instance._unpack();

            switch (arg._unpack(Symbol.class).getValue()) {
                case "get-static-method": {
                    return FW.telephonist(nameV -> {
                        if (!nameV.type().equals(StrFw.str)) return null;
                        String name = nameV._unpack();
                        return FW.telephonist(arg1 -> {
                            if (!arg1.type().equals(StrFw.str)) return null;
                            String descriptor = arg1._unpack();

                            MethodType methodType = MethodType.fromMethodDescriptorString(descriptor, JVMHandles.fwClassLoader);
                            try {
                                return Val.of(JMethodFw.jMethod, JVMHandles.lookup.findStatic(cls, name, methodType));
                            } catch (NoSuchMethodException | IllegalAccessException e) {
                                return Operation.unit;
                            }
                        });
                    });
                }
                case "get-constructor": {
                    return FW.telephonist(arg1 -> {
                        if (!arg1.type().equals(StrFw.str)) return null;
                        String descriptor = arg1._unpack();

                        MethodType methodType = MethodType.fromMethodDescriptorString(descriptor, JVMHandles.fwClassLoader);
                        try {
                            return Val.of(JMethodFw.jMethod, JVMHandles.lookup.findConstructor(cls, methodType));
                        } catch (NoSuchMethodException | IllegalAccessException e) {
                            return Operation.unit;
                        }
                    });
                }
                case "get-static-getter": {
                    return FW.telephonist(nameV -> {
                        if (!nameV.type().equals(StrFw.str)) return null;
                        String name = nameV._unpack();
                        return FW.telephonist(arg1 -> {
                            if (!arg1.type().equals(StrFw.str)) return null;
                            String descriptor = arg1._unpack();
                            try {
                                MethodHandle mh = JVMHandles.lookup.findStaticGetter(cls, name, JVMHandles.findType(descriptor));
                                return Val.of(JMethodFw.jMethod, mh);
                            } catch (IllegalAccessException e) {
                                return Operation.unit;
                            }
                        });
                    });
                }
                case "get-static-setter": {
                    return FW.telephonist(nameV -> {
                        if (!nameV.type().equals(StrFw.str)) return null;
                        String name = nameV._unpack();
                        return FW.telephonist(arg1 -> {
                            if (!arg1.type().equals(StrFw.str)) return null;
                            String descriptor = arg1._unpack();
                            try {
                                return Val.of(JMethodFw.jMethod, JVMHandles.lookup.findStaticSetter(cls, name, JVMHandles.findType(descriptor)));
                            } catch (IllegalAccessException e) {
                                return Operation.unit;
                            }
                        });
                    });
                }
                case "descriptor": {
                    return StrFw.str(JvmUtils.getDescriptor(cls));
                }
                case "canonical-name": {
                    return StrFw.str(cls.getCanonicalName());
                }
                case "is-array": {
                    return BoolFw.wrap(cls.isArray());
                }
                case "superclass": {
                    return wrap(cls.getSuperclass());
                }
                case "is-primitive": {
                    return BoolFw.wrap(cls.isPrimitive());
                }
                case "get-class-oop": {
                    return JVMHandles.jwrap(cls, Class.class);
                }
                case "array-type": {
                    return wrap(Array.newInstance(cls, 0).getClass()); // can be optimized in modern java branch
                }
                case "component-type": {
                    if (!cls.isArray())
                        return null;
                    return wrap(cls.getComponentType());
                }
                case "is-assignable-from": {
                    return FW.telephonist(b -> {
                        if (b.type() != JClassFw.jClass) return null;
                        return BoolFw.wrap(cls.isAssignableFrom(b._unpack(Class.class)));
                    });
                }
                case "Payload": {
                    if (cls.isPrimitive()) {
                        return JClassFw.PRIMITIVE_PAYLOADS.get(cls).asVal();
                    }
                    if (cls.isInterface() || (cls.getModifiers() & Modifier.ABSTRACT) != 0) {
                        return null; // not a type
                    }
                    return JOopFw.jOop.asVal();
                }
            }

            return null;
        }
        return null;
    }).asType();

    public static Val wrap(Class<?> cls) {
        if (cls == null) return null;
        return Val.of(JClassFw.jClass, cls);
    }

    private static final Map<Class<?>, Type> PRIMITIVE_PAYLOADS = new HashMap<>();

    static {
        PRIMITIVE_PAYLOADS.put(void.class, null);
        PRIMITIVE_PAYLOADS.put(byte.class, PrimitiveLayoutsFw.octet);
        PRIMITIVE_PAYLOADS.put(boolean.class, BoolFw.bool);
        PRIMITIVE_PAYLOADS.put(short.class, PrimitiveLayoutsFw.word);
        PRIMITIVE_PAYLOADS.put(char.class, PrimitiveLayoutsFw.word);
        PRIMITIVE_PAYLOADS.put(int.class, PrimitiveLayoutsFw.dword);
        PRIMITIVE_PAYLOADS.put(float.class, PrimitiveLayoutsFw.dword);
        PRIMITIVE_PAYLOADS.put(long.class, PrimitiveLayoutsFw.dqword);
        PRIMITIVE_PAYLOADS.put(double.class, PrimitiveLayoutsFw.dqword);
    }
}
