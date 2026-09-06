package org.fw.core.base;

import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import org.fw.core.util.FwUtils;

public final class BoolFw {
    public static final Type bool = FW.telephonist_native("Bool", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, BoolFw.bool)) {
            Val instance = (Val) CallFw.getVal(arg);
            Value cArg = (Val) CallFw.getArg(arg);

            boolean value = instance._UNPACK(Boolean.class);
            if (cArg.equalsSymbol("not")) {
                return wrap(!value);
            } else if (cArg.equalsSymbol("and")) {
                return bop(instance, (a, b) -> a && b);
            } else if (cArg.equalsSymbol("or")) {
                return bop(instance, (a, b) -> a || b);
            } else if (cArg.equalsSymbol("xor")) {
                return bop(instance, (a, b) -> a != b);
            } else if (cArg.equalsSymbol("if")) {
                return FW.telephonist((arg1) -> { // probably one of the weirdest if implementations ever
                    if (value) return FW.telephonist((arg2) -> arg1);
                    else return FW.telephonist((arg2) -> arg2);
                });
            }
        }
        return null;
    }).asType();

    private static Val bop(Val instance, FwUtils.BoolBinaryOperator operator) {
        boolean value = instance._UNPACK(Boolean.class);
        return FW.telephonist_native((arg1) -> {
            if (arg1.getType().equals(BoolFw.bool)) {
                boolean v2 = arg1._UNPACK(Boolean.class);
                return wrap(operator.apply(value, v2));
            }
            return null;
        });
    }

    public static final Val _true = Val.of(bool, true);
    public static final Val _false = Val.of(bool, false);

    public static Val wrap(boolean x) {
        return x ? _true : _false;
    }

}
