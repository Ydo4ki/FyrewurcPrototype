package org.fw.core.lib;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;

import static org.fw.core.FW.symbol;

public final class BoolFw {
    public static final Type bool = FW.telephonist("Bool", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, BoolFw.bool)) {
            Val instance = Call.getVal(arg);
            Val cArg = Call.getArg(arg);

            boolean value = instance._unpack(Boolean.class);
            if (cArg.equals(symbol("not"))) {
                return wrap(!value);
            } else if (cArg.equals(symbol("and"))) {
                return bop(instance, (a, b) -> a && b);
            } else if (cArg.equals(symbol("or"))) {
                return bop(instance, (a, b) -> a || b);
            } else if (cArg.equals(symbol("xor"))) {
                return bop(instance, (a, b) -> a != b);
            } else if (cArg.equals(symbol("if"))) {
                return FW.telephonist((arg1) -> { // probably one of the weirdest if implementations ever
                    if (value) return FW.telephonist((arg2) -> arg1);
                    else return FW.telephonist((arg2) -> arg2);
                });
            }
        }
        return null;
    }).asType();

    private static Val bop(Val instance, FwUtils.BoolBinaryOperator operator) {
        boolean value = instance._unpack(Boolean.class);
        return FW.telephonist((arg1) -> {
            if (arg1.type().equals(BoolFw.bool)) {
                boolean v2 = arg1._unpack(Boolean.class);
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
