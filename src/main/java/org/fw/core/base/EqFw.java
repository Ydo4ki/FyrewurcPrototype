package org.fw.core.base;

import org.fw.core.FW;
import org.fw.core.util.FwUtils;
import org.fw.core.vit.Vit;
import org.fw.lib.stdlib.constraint._Constraint;

public final class EqFw {

    public static final Val eq = FW.telephonist(
            (arg) -> Val.of(EqFw.eqChecker, arg)
            , (arg) -> _Constraint.of(Vit.call(EqFw.eq, Vit.call(TypeGetFw.typeGet, Vit.var)).call(EqFw.eqChecker.asVal()))
    );
    public static final Type eqChecker = FW.telephonist(arg -> {
        if (FwUtils.isTypeApiCall(arg, EqFw.eqChecker)) {
            Val instance = CallFw.getVal(arg);
            arg = CallFw.getArg(arg);

            Val a = instance._unpack();
            return BoolFw.wrap(arg.equals(a));
        }
        return null;
    }, arg -> {
        if (FwUtils.isTypeApiCall(arg, EqFw.eqChecker)) {
            return _Constraint.of(BoolFw.bool);
        }
        return _Constraint.free;
    }).asType();

    public static Vit eq(Vit a, Vit b) {
        return Vit.call(eq, a).call(b);
    }
}
