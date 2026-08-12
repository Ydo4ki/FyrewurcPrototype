package org.fw.core.jlib;

import org.fw.core.FW;
import org.fw.core.ast.Symbol;
import org.fw.core.base.*;
import org.fw.core.jlib.util.DescriptorUtils;
import org.fw.core.lib.StrFw;
import org.fw.core.state.operation.Operation;
import org.fw.core.util.FwUtils;

import java.lang.invoke.MethodType;
import java.lang.reflect.Array;

public final class JClassFw {
    public static final Type jClass = FW.telephonist((arg) -> {
        if (FwUtils.isTypeApiCall(arg, JClassFw.jClass)) {
            Val instance = Call.getVal(arg);
            arg = Call.getArg(arg);
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
                                return Val.of(JMethodFw.jMethod, JVMHandles.lookup.findStaticGetter(cls, name, JVMHandles.findType(descriptor)));
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
                    return StrFw.str(DescriptorUtils.getDescriptor(cls));
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
            }

            return null;
        }
        return null;
    }).asType();

    public static Val wrap(Class<?> cls) {
        if (cls == null) return null;
        return Val.of(JClassFw.jClass, cls);
    }
}
