package org.fw.base;

import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import org.fw.core.util.FwUtils;

public final class BoolFw {
    public static final Type bool = FW.telephonist_native("Bool", (d) -> {
        Val arg = d.arg();
        if (FwUtils.isTypeApiCall(arg, BoolFw.bool)) {
            Val instance = (Val) CallFw.getVal(arg);
            Value cArg = CallFw.getArg(arg);

            boolean value = d.unpack(instance, Boolean.class);
            if (cArg.equalsSymbol("not")) {
                return wrap(!value);
            } else if (cArg.equalsSymbol("and")) {
                return bop(d, instance, (a, b) -> a && b);
            } else if (cArg.equalsSymbol("or")) {
                return bop(d, instance, (a, b) -> a || b);
            } else if (cArg.equalsSymbol("xor")) {
                return bop(d, instance, (a, b) -> a != b);
            } else if (cArg.equalsSymbol("if")) {
                return FW.lambda((arg1) -> { // probably one of the weirdest if implementations ever
                    if (value) return FW.lambda((arg2) -> arg1);
                    else return FW.lambda((arg2) -> arg2);
                });
            }
        } else if (arg.equalsSymbol("true")) { // check if inited
            return BoolFw._true == null ? d.instance(true) : BoolFw._true;
        } else if (arg.equalsSymbol("false")) {
            return BoolFw._false == null ? d.instance(false) : BoolFw._false;
        }
        return null;
    }).asType();

    private static Val bop(DefinitiveValEnv<Val> d, Val instance, FwUtils.BoolBinaryOperator operator) {
        boolean value = d.unpack(instance, Boolean.class);
        return FW.lambda_native((arg1) -> {
            if (arg1.getType().equals(BoolFw.bool)) {
                boolean v2 = d.unpack(arg1, Boolean.class);
                return wrap(operator.apply(value, v2));
            }
            return null;
        });
    }

    public static final Val _true = (Val) bool.get("true");
    public static final Val _false = (Val) bool.get("false");

    public static Val wrap(boolean x) {
        return x ? _true : _false;
    }

}
