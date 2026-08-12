package org.fw.core.jlib;

import org.fw.core.FW;
import org.fw.core.ast.Symbol;
import org.fw.core.base.Call;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.jlib.util.DescriptorUtils;
import org.fw.core.lib.StrFw;
import org.fw.core.state.operation.Operation;
import org.fw.core.util.FwUtils;

import java.lang.invoke.MethodType;

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
                case "get-descriptor": {
                    return StrFw.str(DescriptorUtils.getDescriptor(cls));
                }
                case "get-class-oop": {
                    return JVMHandles.jwrap(cls, Class.class);
                }
            }

            return null;
        }
        return null;
    }).asType();
}
