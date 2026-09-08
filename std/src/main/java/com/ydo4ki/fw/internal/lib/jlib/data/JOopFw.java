package com.ydo4ki.fw.internal.lib.jlib.data;

import com.ydo4ki.esast.Expr;
import org.fw.core.FW;
import com.ydo4ki.esast.Symbol;
import org.fw.base.CallFw;
import org.fw.base.SymbolFw;
import org.fw.base.Type;
import org.fw.base.Val;
import com.ydo4ki.fw.internal.lib.jlib._internal.JClassFw;
import com.ydo4ki.fw.internal.lib.jlib._internal.JMethodFw;
import com.ydo4ki.fw.internal.lib.jlib._internal.JVMHandles;
import com.ydo4ki.fw.internal.lib.stdlib.StrFw;
import org.fw.core.state.operation.Operation;
import org.fw.core.util.FwUtils;
import org.fw.esast.expr.ExprFw;

import java.lang.invoke.MethodType;

public final class JOopFw {
    public static final Type jOop = FW.telephonist_native((arg) -> {
        if (FwUtils.isTypeApiCall(arg, JOopFw.jOop)) {
            Val instance = (Val) CallFw.getVal(arg);
            arg = (Val) CallFw.getArg(arg);
            if (arg.getType() != SymbolFw.symbol)
                return null;

            Object oop = instance._UNPACK_();
            Class<?> cls = oop.getClass();

            switch (((Symbol) ExprFw.unwrap(arg)).getValue()) {
                case "get-method": {
                    return FW.telephonist_native(nameV -> {
                        if (!nameV.getType().equals(StrFw.str)) return null;
                        String name = nameV._UNPACK_();
                        return FW.telephonist_native(arg1 -> {
                            if (!arg1.getType().equals(StrFw.str)) return null;
                            String descriptor = arg1._UNPACK_();

                            MethodType methodType = MethodType.fromMethodDescriptorString(descriptor, JVMHandles.fwClassLoader);
                            try {
                                return Val._NEW_INSTANCE_(JMethodFw.jMethod, JVMHandles.lookup.findVirtual(cls, name, methodType).bindTo(oop));
                            } catch (NoSuchMethodException | IllegalAccessException e) {
                                return Operation.unit;
                            }
                        });
                    });
                }
                // todo: find-method-polymorphic
                case "get-class": {
                    return JClassFw.wrap(oop.getClass());
                }
                case "identity-hash-code": {
                    return JIntFw.wrap(System.identityHashCode(oop));
                }
                case "typed": {
                    return Val._NEW_INSTANCE_(JClassFw.wrap(oop.getClass()).asType(), oop);
                }
            }

            return null;
        }
        return null;
    }).asType();
}
