package org.fw.core.jlib;

import org.fw.core.FW;
import org.fw.core.ast.Symbol;
import org.fw.core.base.Call;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.*;
import org.fw.core.lib.expr.ExprFw;
import org.fw.core.lib.state.SystemOperation;
import org.fw.core.state.obj.Obj;
import org.fw.core.state.operation.Operation;
import org.fw.core.util.FwUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static org.fw.core.FW.symbol;

public final class JVMHandles {
    private static final MethodHandles.Lookup lookup = MethodHandles.lookup();
    public static final Type jClass = FW.telephonist((arg) -> {
        if (FwUtils.isTypeApiCall(arg, JVMHandles.jClass)) {
            Val instance = Call.getVal(arg);
            arg = Call.getArg(arg);
            if (arg.type() != SymbolFw.symbol)
                return null;

            Class<?> cls = instance._unpack();

            switch (arg._unpack(Symbol.class).getValue()) {
                case "find-static-method": {
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
                                        return Val.of(JVMHandles.jMethod, lookup.findStatic(cls, name, methodType));
                                    } catch (NoSuchMethodException | IllegalAccessException e) {
                                        return Operation.unit;
                                    }
                                }
                            }.asVal();
                        });
                    });
                }
            }

            return null;
        }
        return null;
    }).asType();
    public static final Type jMethod = FW.telephonist((arg) -> {
        if (FwUtils.isTypeApiCall(arg, JVMHandles.jMethod)) {
            Val instance = Call.getVal(arg);
            arg = Call.getArg(arg);
            if (arg.type() != SymbolFw.symbol)
                return null;

            MethodHandle method = instance._unpack();

            switch (arg._unpack(Symbol.class).getValue()) {
                case "invoke-method": {
                    return FW.telephonist(argumentsVec -> {
                        if (argumentsVec.type() != DVecFw.dVec)
                            return null;
                        Val[] arguments = argumentsVec._unpack();
                        Object[] jArgs = new Object[arguments.length];
                        for (int i = 0; i < arguments.length; i++) {
                            Val argument = arguments[i];
                            if (argument.type() == DIntFw.dint) jArgs[i] = DIntFw.unwrap(argument).intValue();
                            else jArgs[i] = argument;
                        }
                        return new SystemOperation() {
                            @Override
                            protected Val execute0() {
                                try {
                                    return wrap(method.invokeWithArguments((Object[]) jArgs));
                                } catch (Throwable e) {
                                    e.printStackTrace();
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
    public static final Type jOop = FW.telephonist((arg) -> {
        return null;
    }).asType();
    public static final Val jvmEnv = ModuleFw.module(
            DeclaredFw.declared(symbol("get-class"), FW.telephonist((arg) -> {
                if (!arg.type().equals(StrFw.str))
                    return null;
                String name = arg._unpack();
                return new SystemOperation() {
                    @Override
                    protected Val execute0() {
                        Class<?> cls = null;
                        try {
                            cls = Class.forName(name);
                        } catch (ClassNotFoundException e) {
                            return Operation.unit;
                        }
                        return Val.of(jClass, cls);
                    }
                }.asVal();
            }))
    );

    private static Val wrap(Object jObj) {
        return Operation.unit;
    }
}
