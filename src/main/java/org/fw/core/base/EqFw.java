package org.fw.core.base;

import org.fw.core.FW;
import org.fw.core.base.contract.CallContract;
import org.fw.core.util.FwUtils;
import org.fw.core.vit.Vit;

public final class EqFw {

    public static final Val eq = FW.telephonist(
            (arg) -> Val.of(EqFw.eqChecker, arg)
            , CallContract.c((arg) -> Constraint.of(Vit.call(EqFw.eq, Vit.call(TypeGetFw.typeGet, Vit.var)).call(EqFw.eqChecker.asVal())))
    );
    public static final Type eqChecker = FW.telephonist(arg -> {
        if (FwUtils.isTypeApiCall(arg, EqFw.eqChecker)) {
            Val instance = CallFw.getVal(arg);
            arg = CallFw.getArg(arg);

            Val a = instance._unpack();
            return BoolFw.wrap(arg.equals(a));
        }
        return null;
    }, CallContract.c(arg -> {
        if (FwUtils.isTypeApiCall(arg, EqFw.eqChecker)) {
            return Constraint.type(BoolFw.bool);
        }
        return Constraint.free;
    })).asType();

    public static Vit eq(Vit a, Vit b) {
        return Vit.call(eq, a).call(b);
    }
}
