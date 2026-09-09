package org.fw.std;

import org.fw.base.CallFw;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.core.FW;

import org.fw.core.util.FwUtils;

import static org.fw.core.FW.symbol;

public final class BoxFw {
    public static final Type boxType = FW.lambda_native("BoxType", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, BoxFw.boxType)) {
            Val instance = (Val) CallFw.getVal(arg);
            arg = (Val) CallFw.getArg(arg);

            Type type = instance.asType();
            if (FwUtils.isTypeApiCall(arg, type)) {
                instance = (Val) CallFw.getVal(arg);
                Val cArg = (Val) CallFw.getArg(arg);
                if (cArg.equalsSymbol("unbox")) {
                    return unbox(instance);
                }
            } else if (arg.equalsSymbol("construct")) {
                return FW.lambda_native((arg1) -> Val._NEW_INSTANCE_(type, arg1));
            }
            return null;
        } else if (arg.equalsSymbol("construct")) {
            return FW.lambda_native(arg1 -> Val._NEW_INSTANCE_(BoxFw.boxType, arg1));
        }
        return null;
    }).asType();

    // the only operation that doesn't need context xd
    public static Val unbox(Val arg) {
        return arg._UNPACK_();
    }

    public static Type newBoxType(Val key) {
        Val val = boxType.asVal();
        Val val1 = ((Val) val.call(symbol("construct")));
        return ((Val) val1.call(key)).asType();
    }

}
