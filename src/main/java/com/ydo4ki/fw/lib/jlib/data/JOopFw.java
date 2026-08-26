package com.ydo4ki.fw.lib.jlib.data;

import org.fw.core.FW;
import org.fw.core.ast.Symbol;
import org.fw.core.base.CallFw;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import com.ydo4ki.fw.lib.jlib._internal.JClassFw;
import com.ydo4ki.fw.lib.jlib._internal.JMethodFw;
import com.ydo4ki.fw.lib.jlib._internal.JVMHandles;
import org.fw.lib.stdlib.StrFw;
import org.fw.core.state.operation.Operation;
import org.fw.core.util.FwUtils;

import java.lang.invoke.MethodType;

public final class JOopFw {
    public static final Type jOop = FW.telephonist((arg) -> {
        if (FwUtils.isTypeApiCall(arg, JOopFw.jOop)) {
            Val instance = CallFw.getVal(arg);
            arg = CallFw.getArg(arg);
            if (arg.type() != SymbolFw.symbol)
                return null;

            Object oop = instance._unpack();
            Class<?> cls = oop.getClass();

            switch (arg._unpack(Symbol.class).getValue()) {
                case "get-method": {
                    return FW.telephonist(nameV -> {
                        if (!nameV.type().equals(StrFw.str)) return null;
                        String name = nameV._unpack();
                        return FW.telephonist(arg1 -> {
                            if (!arg1.type().equals(StrFw.str)) return null;
                            String descriptor = arg1._unpack();

                            MethodType methodType = MethodType.fromMethodDescriptorString(descriptor, JVMHandles.fwClassLoader);
                            try {
                                return Val.of(JMethodFw.jMethod, JVMHandles.lookup.findVirtual(cls, name, methodType).bindTo(oop));
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
                    return Val.of(JClassFw.wrap(oop.getClass()).asType(), oop);
                }
            }

            return null;
        }
        return null;
    }).asType();
}
