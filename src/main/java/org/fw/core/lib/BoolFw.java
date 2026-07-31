package org.fw.core.lib;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.base.context.Context;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;

import static org.fw.core.FW.symbol;

public final class BoolFw {
    public static final Type bool = FW.telephonist("Bool", (arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, BoolFw.bool, context)) {
            Val instance = Call.getVal(arg, context);
            Val cArg = Call.getArg(arg, context);

            boolean value = instance._unpack(Boolean.class);
            if (cArg.equals(symbol("not"))) {
                return wrap(!value);
            } else if (cArg.equals(symbol("and"))) {
                return bop("and", instance, context, (a, b) -> a && b);
            } else if (cArg.equals(symbol("or"))) {
                return bop("or", instance, context, (a, b) -> a || b);
            } else if (cArg.equals(symbol("xor"))) {
                return bop("xor", instance, context, (a, b) -> a != b);
            } else if (cArg.equals(symbol("if"))) {
                return FW.telephonistE(() -> callReprs("if", instance, context), (arg1, context1) -> { // probably one of the weirdest if implementations ever
                    if (value)
                        return FW.telephonist(ExprList.of(BracketsTypes.round, Symbol.of("call"), instance.toExpr(context), Symbol.of("if"), arg1.toExpr(context)), (arg2, context2) -> {
                            return arg1;
                        });
                    else
                        return FW.telephonist(ExprList.of(BracketsTypes.round, Symbol.of("call"), instance.toExpr(context), Symbol.of("if"), arg1.toExpr(context)), (arg2, context2) -> {
                            return arg2;
                        });
                });
            }
        }
        return null;
    }).asType();

    private static Val bop(String name, Val instance, Context context, FwUtils.BoolBinaryOperator operator) {
        boolean value = instance._unpack(Boolean.class);
        return FW.telephonist(callReprs(name, instance, context), (arg1, c) -> {
            if (arg1.type().equals(BoolFw.bool)) {
                boolean v2 = arg1._unpack(Boolean.class);
                return wrap(operator.apply(value, v2));
            }
            return null;
        });
    }

    private static Expr callReprs(String op, Val instance, Context context) {
        return ExprList.of(BracketsTypes.round, Symbol.of("call"), instance.toExpr(context), Symbol.of(op));
    }

    public static final Val _true = Val.of(bool, true);
    public static final Val _false = Val.of(bool, false);

    public static Val wrap(boolean x) {
        return x ? _true : _false;
    }

}
