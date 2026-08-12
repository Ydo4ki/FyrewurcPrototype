package org.fw.core.jlib.data;

import org.fw.core.FW;
import org.fw.core.ast.Symbol;
import org.fw.core.base.Call;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.jlib.JClassFw;
import org.fw.core.jlib.JMethodFw;
import org.fw.core.jlib.JVMHandles;
import org.fw.core.lib.StrFw;
import org.fw.core.state.operation.Operation;
import org.fw.core.util.FwUtils;

import java.lang.invoke.MethodType;

public final class JOopFw {
    public static final Type jOop = FW.telephonist((arg) -> {
        if (FwUtils.isTypeApiCall(arg, JOopFw.jOop)) {
            Val instance = Call.getVal(arg);
            arg = Call.getArg(arg);
            if (arg.type() != SymbolFw.symbol)
                return null;

            Object oop = instance._unpack();
            Class<?> cls = oop.getClass();

            switch (arg._unpack(Symbol.class).getValue()) {
                case "find-method": {
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
            }

            return null;
        }
        return null;
    }).asType();
}
