package org.fw.base;

import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import org.fw.core.util.FwUtils;
import org.fw.core.vit.Vit;

public final class EqFw {

    public static final Val eq;
    public static final Type eqChecker;

    static {
        eq = FW.lambda("eq", (arg1) -> Val.of(EqFw.eqChecker, arg1));
        eqChecker = FW.lambda_native("eqChecker", arg -> {
            if (FwUtils.isTypeApiCall(arg, EqFw.eqChecker)) {
                Val instance = CallFw.getVal(arg).asVal();
                arg = CallFw.getArg(arg).asVal();

                Value a = (Value) instance.getValue();
                return BoolFw.wrap(a.impliesEquality(arg));
            }
            return null;
        }).asType();
    }

    public static Vit eq(Vit a, Vit b) {
        return Vit.call(eq, a).call(b);
    }
}
