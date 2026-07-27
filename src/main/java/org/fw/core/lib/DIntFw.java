package org.fw.core.lib;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.vit.Vit;

import java.math.BigInteger;

import static org.fw.core.FW.symbol;
import static org.fw.core.vit.Vit.val;

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
                        return Unspecified.unspecified;
                    }
                }
                return Unspecified.unspecified;
            });
        }
        return Unspecified.unspecified;
    }).asType();

    private static Val bop(String name, Val instance, Context context, FwUtils.BigBinaryOperator operator) {
        BigInteger value = unwrap(instance);
        assert value != null;
        return FW.telephonist(callReprs(name, instance, context), (arg1, context1) -> {
            if (arg1.type().equals(DIntFw.dint)) {
                BigInteger v2 = unwrap(arg1);
                return dint(operator.apply(value, v2));
            }
            return Unspecified.unspecified;
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

    public static final class ParseDIntCEnvFw {
        public static final Val parseNumCenv = StrFw.ParseStrCEnvFw.symbolMapEnv(val(FW.telephonist("parseNum", (arg1, context1) -> {
            return Vit.val(dint.asVal()).call(symbol("parse")).call(arg1.call(symbol("value"), context1))
                    .eval(context1);
        })));
    }

    public static CompEnv exports = CompEnv.of(CompEnv.compEnv(Context.outOf,
            ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                    DeclaredFw.declared(symbol("DInt"), DIntFw.dint.asVal()),
                    DeclaredFw.declared(symbol("parseDIntCEnv"), ParseDIntCEnvFw.parseNumCenv)
            )),
            ParseDIntCEnvFw.parseNumCenv
    ));
}
