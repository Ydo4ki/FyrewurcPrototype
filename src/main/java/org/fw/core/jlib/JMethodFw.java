package org.fw.core.jlib;

import org.fw.core.FW;
import org.fw.core.ast.Symbol;
import org.fw.core.base.Call;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.jlib.data.JIntFw;
import org.fw.core.lib.DIntFw;
import org.fw.core.lib.dvec.DVecFw;
import org.fw.core.lib.state.SystemOperation;
import org.fw.core.state.operation.Operation;
import org.fw.core.util.FwUtils;

import java.lang.invoke.MethodHandle;

public final class JMethodFw {
    public static final Type jMethod = FW.telephonist((arg) -> {
        if (FwUtils.isTypeApiCall(arg, JMethodFw.jMethod)) {
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
                            if (argument.type() == DIntFw.dint) {
                                jArgs[i] = DIntFw.unwrap(argument).intValue(); // temp
                            } else if (argument.type() == JIntFw.jint) {
                                jArgs[i] = JIntFw.unwrap(argument);
                            } else {
                                jArgs[i] = argument;
                            }
                        }
                        return new SystemOperation() {
                            @Override
                            protected Val execute0() {
                                try {
                                    return JVMHandles.wrap(method.invokeWithArguments((Object[]) jArgs), method.type().returnType());
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
}
