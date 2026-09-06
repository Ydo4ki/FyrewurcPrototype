package com.ydo4ki.fw.internal.lib.jlib._internal;

import org.fw.core.FW;
import org.fw.core.ast.Symbol;
import org.fw.core.base.CallFw;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.lib.stdlib.dvec.DVecFw;
import org.fw.lib.stdlib.expr.SyntaxResolveFw;
import com.ydo4ki.fw.internal.lib.stdlib.state.SystemOperation;
import org.fw.core.state.operation.Operation;
import org.fw.core.util.FwUtils;

import java.lang.invoke.MethodHandle;

public final class JMethodFw {
    public static final Type jMethod = FW.telephonist_native((arg) -> {
        if (FwUtils.isTypeApiCall(arg, JMethodFw.jMethod)) {
            Val instance = (Val) CallFw.getVal(arg);
            arg = (Val) CallFw.getArg(arg);
            if (arg.getType() != SymbolFw.symbol)
                return null;

            MethodHandle method = instance._UNPACK();

            switch (arg._UNPACK(Symbol.class).getValue()) {
                case "invoke-method": {
                    return FW.telephonist_native(argumentsVec -> {
                        if (argumentsVec.getType() != DVecFw.dVec)
                            return null;

                        Val[] arguments = argumentsVec._UNPACK();
                        Object[] jArgs = new Object[arguments.length];
                        for (int i = 0; i < arguments.length; i++) {
                            Val argument = arguments[i];
                            jArgs[i] = JVMHandles.junwrap(argument);
                        }
                        return new SystemOperation() {
                            @Override
                            protected Val apply0() {
                                try {
                                    return JVMHandles.jwrap(method.invokeWithArguments((Object[]) jArgs), method.type().returnType());
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


    public static final Val methodCallCEnv = FW.telephonist_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.toFnResolve)) {
            Val val = arg.get("passing");
            Val compEnv = arg.get("chain");
            if (val.getType() == JMethodFw.jMethod) {
                return val.get("invoke-method");
            }
        }
        return null;
    });
}
