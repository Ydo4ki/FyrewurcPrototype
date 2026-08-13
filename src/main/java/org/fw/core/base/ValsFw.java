package org.fw.core.base;

import org.fw.core.FW;
import org.fw.core.util.FwUtils;
import org.fw.core.vit.Vit;

public final class ValsFw {
    // basic stuff

    public static final Val typeGet = FW.telephonist(
            (arg) -> arg.type().asVal()
//            , (arg) -> Constraint.free
    );

    public static final Val eq = FW.telephonist(
            (arg) -> Val.of(ValsFw.eqChecker, arg)
//            , (arg) -> Constraint.of(Vit.call(ValsFw.eq, Vit.call(ValsFw.typeGet, Vit.var)).call(ValsFw.eqChecker.asVal()))
    );

    public static final Type eqChecker = FW.telephonist(arg -> {
        if (FwUtils.isTypeApiCall(arg, ValsFw.eqChecker)) {
            Val instance = Call.getVal(arg);
            arg = Call.getArg(arg);

            Val a = instance._unpack();
            return BoolFw.wrap(arg.equals(a));
        }
        return null;
    }/*, arg -> {
        if (FwUtils.isTypeApiCall(arg, ValsFw.eqChecker)) {
            return Constraint.of(BoolFw.bool);
        }
        return Constraint.free;
    }*/).asType();

    public static Vit eq(Vit a, Vit b) {
        return Vit.call(eq, a).call(b);
    }

    public static final Val isUnspecified = FwUtils.valify(Unspecified::isUnspecified);
}