package org.fw.core.base;

import org.fw.core.FW;
import org.fw.core.util.FwUtils;
import org.fw.core.vit.Vit;

public final class EqFw {

    public static final Val eq;
    public static final Type eqChecker;

    static {
        eq = FW.telephonist((arg1) -> Val.of(EqFw.eqChecker, arg1));
        eqChecker = FW.telephonist(arg -> {
            if (FwUtils.isTypeApiCall(arg, EqFw.eqChecker)) {
                Val instance = CallFw.getVal(arg);
                arg = CallFw.getArg(arg);

                Val a = instance._unpack();
                return BoolFw.wrap(arg.equals(a));
            }
            return null;
        }).asType();
    }

    public static Vit eq(Vit a, Vit b) {
        return Vit.call(eq, a).call(b);
    }
}
