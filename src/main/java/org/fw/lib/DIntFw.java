package org.fw.lib;

import org.fw.FW;
import org.fw.FwUtils;
import org.fw.ast.BracketsTypes;
import org.fw.ast.Expr;
import org.fw.ast.ExprList;
import org.fw.ast.Symbol;
import org.fw.base.Call;
import org.fw.base.Context;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.lib.expr.ExprFw;

import java.math.BigInteger;

import static org.fw.FW.symbol;

public final class DIntFw {
    public static final Type dint = FW.telephonist("DInt", (arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, DIntFw.dint, context)) {
            Val instance = Call.getVal(arg, context);
            Val cArg = Call.getArg(arg, context);

            BigInteger value = unwrap(instance);
            assert value != null;
            if (cArg.equals(symbol("neg"))) {
                return dint(value.negate());
            } else if (cArg.equals(symbol("+"))) {
                return bop("+", instance, context, BigInteger::add);
            } else if (cArg.equals(symbol("-"))) {
                return bop("-", instance, context, BigInteger::subtract);
            } else if (cArg.equals(symbol("*"))) {
                return bop("*", instance, context, BigInteger::multiply);
            } else if (cArg.equals(symbol("/"))) {
                return bop("/", instance, context, BigInteger::divide);
            } else if (cArg.equals(symbol("%"))) {
                return bop("%", instance, context, BigInteger::mod);
            } else if (cArg.equals(symbol("<<"))) {
                return bop("<<", instance, context, (a, b) -> b.bitLength() > 32 ? BigInteger.ZERO : a.shiftLeft(b.intValue()));
            } else if (cArg.equals(symbol(">>"))) {
                return bop(">>", instance, context, (a, b) -> b.bitLength() > 32 ? BigInteger.ZERO : a.shiftRight(b.intValue()));
            } else if (cArg.equals(symbol("<=>"))) {
                return bop("<=>", instance, context, (a, b) -> BigInteger.valueOf(a.compareTo(b)));
            }
        } else if (arg.equals(symbol("parse"))) {
            return FW.telephonist(ExprList.of(BracketsTypes.round, Symbol.of("get"), DIntFw.dint.asVal().toExpr(context), Symbol.of("parse")), (arg1, context1) -> {
                if (arg1.type().equals(StrFw.str)) {
                    String string = arg1._unpack();
                    try {
                        BigInteger i = new BigInteger(string);
                        return dint(i);
                    } catch (NumberFormatException e) {
                        return Val.unspecified;
                    }
                }
                return Val.unspecified;
            });
        }
        return Val.unspecified;
    }).asType();

    private static Val bop(String name, Val instance, Context context, FwUtils.BigBinaryOperator operator) {
        BigInteger value = unwrap(instance);
        assert value != null;
        return FW.telephonist(callReprs(name, instance, context), (arg1, context1) -> {
            if (arg1.type().equals(DIntFw.dint)) {
                BigInteger v2 = unwrap(arg1);
                return dint(operator.apply(value, v2));
            }
            return Val.unspecified;
        });
    }

    private static Expr callReprs(String op, Val instance, Context context) {
        return ExprList.of(BracketsTypes.round, Symbol.of("call"), instance.toExpr(context), Symbol.of(op));
    }

    public static Val dint(long value) {
        return dint(BigInteger.valueOf(value));
    }

    public static Val dint(BigInteger value) {
        return Val.of(dint, value);
    }

    public static BigInteger unwrap(Val dint) {
        if (dint.type().equals(DIntFw.dint)) return unwrap0(dint);
        return null;
    }

    public static BigInteger unwrap0(Val dint) {
        return dint._unpack();
    }
}
