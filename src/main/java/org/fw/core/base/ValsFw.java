package org.fw.core.base;

import org.fw.core.FW;
import org.fw.core.ast.Symbol;
import org.fw.core.util.FwUtils;

public final class ValsFw {
    // basic stuff

    public static final Val typeGet = FW.telephonist(
            Symbol.of("type-get"),
            (arg) -> arg.type().asVal()
    );

    public static final Val eq = FW.telephonist(
            Symbol.of("eq"),
            (arg) -> Val.of(ValsFw.eqChecker, arg)
    );

    public static final Type eqChecker = FW.telephonist(arg -> {
        if (FwUtils.isTypeApiCall(arg, ValsFw.eqChecker)) {
            Val instance = Call.getVal(arg);
            arg = Call.getArg(arg);

            Val a = instance._unpack();
            return BoolFw.wrap(arg.equals(a));
        }
        return null;
    }).asType();

    public static final Val isUnspecified = FwUtils.valify(Unspecified::isUnspecified);
}