package org.fw.lib.stdlib.dvec;

import org.fw.core.FW;
import org.fw.core.base.CallFw;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.util.FwUtils;

public final class DVecIterFw {
    public static final Type dVecIter = FW.telephonist(arg -> {
        if (FwUtils.isTypeApiCall(arg, DVecIterFw.dVecIter)) {
            Val iterTypeInstance = CallFw.getVal(arg);
            arg = CallFw.getArg(arg);

            Type iterType = iterTypeInstance.asType();
            Val targetDVec = iterTypeInstance._unpack();
            Val[] target = targetDVec._unpack();
            if (arg.getType() == SymbolFw.symbol) {
                String s = arg._unpack().toString();
                switch (s) {
                    case "target":
                        return targetDVec;
                }
            }
            if (FwUtils.isTypeApiCall(arg, iterType)) {
                Val instance = CallFw.getVal(arg);
                arg = CallFw.getArg(arg);

                int i = instance._unpack();
                if (arg.getType().equals(SymbolFw.symbol)) {
                    String text = arg._unpack().toString();
                    switch (text) {
                        case "value":
                            return target[i];
                        case "prev":
                            if (i > 0) return Val.of(iterType, i - 1);
                        case "next":
                            if (i < target.length - 1) return Val.of(iterType, i + 1);
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
        return Val.of(dVecIter, dVec).asType();
    }

    public static Val iterator(Val dVec, int i) {
        Type type = iterType(dVec);
        return Val.of(type, i);
    }
}
