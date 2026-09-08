package org.fw.std.dvec;

import org.fw.core.FW;
import org.fw.base.CallFw;
import org.fw.base.SymbolFw;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.core.util.FwUtils;

public final class DVecIterFw {
    public static final Type dVecIter = FW.lambda_native(arg -> {
        if (FwUtils.isTypeApiCall(arg, DVecIterFw.dVecIter)) {
            Val iterTypeInstance = (Val) CallFw.getVal(arg);
            arg = (Val) CallFw.getArg(arg);

            Type iterType = iterTypeInstance.asType();
            Val targetDVec = iterTypeInstance._UNPACK_();
            Val[] target = targetDVec._UNPACK_();
            if (arg.getType() == SymbolFw.symbol) {
                String s = arg._UNPACK_().toString();
                switch (s) {
                    case "target":
                        return targetDVec;
                }
            }
            if (FwUtils.isTypeApiCall(arg, iterType)) {
                Val instance = (Val) CallFw.getVal(arg);
                arg = (Val) CallFw.getArg(arg);

                int i = instance._UNPACK_();
                if (arg.getType().equals(SymbolFw.symbol)) {
                    String text = arg._UNPACK_().toString();
                    switch (text) {
                        case "value":
                            return target[i];
                        case "prev":
                            if (i > 0) return Val._NEW_INSTANCE_(iterType, i - 1);
                        case "next":
                            if (i < target.length - 1) return Val._NEW_INSTANCE_(iterType, i + 1);
                    }
                    return null;
                }
                return null;
            }
            return null;
        }
        return null;
    }).asType();

    public static Type iterType(Val dVec) {
        if (dVec.getType() != DVecFw.dVec)
            throw new IllegalArgumentException(dVec.toString());
        return Val._NEW_INSTANCE_(dVecIter, dVec).asType();
    }

    public static Val iterator(Val dVec, int i) {
        Type type = iterType(dVec);
        return Val._NEW_INSTANCE_(type, i);
    }
}
