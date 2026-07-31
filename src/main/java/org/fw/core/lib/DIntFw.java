package org.fw.core.lib;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.base.context.Context;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.ExprFw;
import org.fw.core.util.FwUtils;
import org.fw.core.vit.Vit;

import java.math.BigInteger;

import static org.fw.core.FW.symbol;
import static org.fw.core.vit.Vit.val;

public final class DIntFw {
    public static final Type dint = FW.telephonist("DInt", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, DIntFw.dint)) {
            Val instance = Call.getVal(arg);
            Val cArg = Call.getArg(arg);

            BigInteger value = unwrap(instance);
            assert value != null;
            if (cArg.equals(symbol("neg"))) {
                return dint(value.negate());
            } else if (cArg.equals(symbol("+"))) {
                return bop(instance, BigInteger::add);
            } else if (cArg.equals(symbol("-"))) {
                return bop(instance, BigInteger::subtract);
            } else if (cArg.equals(symbol("*"))) {
                return bop(instance, BigInteger::multiply);
            } else if (cArg.equals(symbol("/"))) {
                return bop(instance, BigInteger::divide);
            } else if (cArg.equals(symbol("%"))) {
                return bop(instance, BigInteger::mod);
            } else if (cArg.equals(symbol("<<"))) {
                return bop(instance, (a, b) -> b.bitLength() > 32 ? BigInteger.ZERO : a.shiftLeft(b.intValue()));
            } else if (cArg.equals(symbol(">>"))) {
                return bop(instance, (a, b) -> b.bitLength() > 32 ? BigInteger.ZERO : a.shiftRight(b.intValue()));
            } else if (cArg.equals(symbol("<=>"))) {
                return bop(instance, (a, b) -> BigInteger.valueOf(a.compareTo(b)));
            }
        } else if (arg.equals(symbol("parse"))) {
            return FW.telephonist((arg1) -> {
                if (arg1.type().equals(StrFw.str)) {
                    String string = arg1._unpack();
                    try {
                        BigInteger i = new BigInteger(string);
                        return dint(i);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
                return null;
            });
        }
        return null;
    }).asType();
    public static final Val dintToExpr = FW.telephonist((arg) -> {
        Type type = arg.type();
        if (type.equals(dint)) {
            return symbol(arg._unpack().toString());
        }
        return null;
    });

    private static Val bop(Val instance, FwUtils.BigBinaryOperator operator) {
        BigInteger value = unwrap(instance);
        assert value != null;
        return FW.telephonist((arg1) -> {
            if (arg1.type().equals(DIntFw.dint)) {
                BigInteger v2 = unwrap(arg1);
                return dint(operator.apply(value, v2));
            }
            return null;
        });
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
        public static final Val parseNumCenv = StrFw.ParseStrCEnvFw.symbolMapEnv(val(FW.telephonist("parseNum", (arg1) -> {
            return Vit.val(dint.asVal()).call(symbol("parse")).call(ExprFw.symbolToString.call(arg1))
                    .eval();
        })));
    }

    public static CompEnv exports = CompEnv.of(CompEnv.compEnv(
            ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                    DeclaredFw.declared(symbol("DInt"), DIntFw.dint.asVal()),
                    DeclaredFw.declared(symbol("parseDIntCEnv"), ParseDIntCEnvFw.parseNumCenv)
            )),
            ParseDIntCEnvFw.parseNumCenv
    ));
}
