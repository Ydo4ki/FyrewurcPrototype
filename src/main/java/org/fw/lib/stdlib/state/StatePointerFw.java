package org.fw.lib.stdlib.state;

import org.fw.core.FW;
import org.fw.core.base.CallFw;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.GetLocalStateOperation;
import org.fw.core.util.FwUtils;

public final class StatePointerFw {
    public static final Type statePointer = FW.telephonist("StatePointer", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, StatePointerFw.statePointer)) {
            Val instance = CallFw.getVal(arg);
            arg = CallFw.getArg(arg);

            State obj = instance._unpack();

            if (arg.getType() == SymbolFw.symbol) {
                String s = arg._unpack().toString();
                switch (s) {
                    case "scope":
                        return obj.scope().asVal();
                }
            }
            return null;
        } else {
            if (arg.getType() == SymbolFw.symbol) {
                String s = arg._unpack().toString();
                if (s.equals("current")) {
                    return GetLocalStateOperation.getInstance().asVal();
                }
            }
        }
        return null;
    }).asType();
}

