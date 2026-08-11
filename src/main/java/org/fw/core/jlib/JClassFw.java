package org.fw.core.jlib;

import org.fw.core.FW;
import org.fw.core.ast.Symbol;
import org.fw.core.base.Call;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.StrFw;
import org.fw.core.lib.state.SystemOperation;
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
                case "find-method": {
                    return FW.telephonist(nameV -> {
                        if (!nameV.type().equals(StrFw.str)) return null;
                        String name = nameV._unpack();
                        return FW.telephonist(arg1 -> {
                            if (!arg1.type().equals(StrFw.str)) return null;
                            String descriptor = arg1._unpack();

                            return new SystemOperation() {
                                @Override
                                protected Val execute0() {
                                    MethodType methodType = MethodType.fromMethodDescriptorString(descriptor, ClassLoader.getSystemClassLoader());
                                    try {
                                        return Val.of(JMethodFw.jMethod, JVMHandles.lookup.findStatic(cls, name, methodType));
                                    } catch (NoSuchMethodException | IllegalAccessException e) {
                                        return Operation.unit;
                                    }
                                }
                            }.asVal();
                        });
                    });
                }
                case "find-constructor": {
                    return FW.telephonist(arg1 -> {
                        if (!arg1.type().equals(StrFw.str)) return null;
                        String descriptor = arg1._unpack();

                        return new SystemOperation() {
                            @Override
                            protected Val execute0() {
                                MethodType methodType = MethodType.fromMethodDescriptorString(descriptor, ClassLoader.getSystemClassLoader());
                                try {
                                    return Val.of(JMethodFw.jMethod, JVMHandles.lookup.findConstructor(cls, methodType));
                                } catch (NoSuchMethodException | IllegalAccessException e) {
                                    return Operation.unit;
                                }
                            }
                        }.asVal();
                    });
                }
            }

            return null;
        }
        return null;
    }).asType();
}
