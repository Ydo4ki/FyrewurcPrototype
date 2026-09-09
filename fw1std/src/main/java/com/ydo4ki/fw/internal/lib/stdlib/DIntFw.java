package com.ydo4ki.fw.internal.lib.stdlib;

import org.fw.base.*;
import org.fw.core.FW;
import org.fw.core.util.FwUtils;

import java.math.BigInteger;

// lmao I completely forgot we wanted to get rid of this
public final class DIntFw {
    public static final Type dint = FW.lambda_native("DInt", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, DIntFw.dint)) {
            Val instance = (Val) CallFw.getVal(arg);
            Val cArg = (Val) CallFw.getArg(arg);

            BigInteger value = unwrap(instance);
            assert value != null;
            if (cArg.getType() == SymbolFw.symbol) {
                String sym = cArg._UNPACK_().toString();
                switch (sym) {
                    case "neg":
                        return dint(value.negate());
                    case "+":
                        return bop(instance, BigInteger::add);
                    case "-":
                        return bop(instance, BigInteger::subtract);
                    case "*":
                        return bop(instance, BigInteger::multiply);
                    case "/":
                        return bop(instance, BigInteger::divide);
                    case "%":
                        return bop(instance, BigInteger::mod);
                    case "<<":
                        return bop(instance, (a, b) -> b.bitLength() > 32 ? BigInteger.ZERO : a.shiftLeft(b.intValue()));
                    case ">>":
                        return bop(instance, (a, b) -> b.bitLength() > 32 ? BigInteger.ZERO : a.shiftRight(b.intValue()));
                    case "<=>":
                        return bop(instance, (a, b) -> BigInteger.valueOf(a.compareTo(b)));
                }
            }
        } else if (arg.equalsSymbol("parse")) {
            return FW.lambda_native((arg1) -> {
//                if (arg1.getType().equals(StrFw.str)) {
                if (arg1._UNPACK_() instanceof String) {
                    String string = arg1._UNPACK_();
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

    private static Val bop(Val instance, FwUtils.BigBinaryOperator operator) {
        BigInteger value = unwrap(instance);
        assert value != null;
        return FW.lambda_native((arg1) -> {
            if (arg1.getType().equals(DIntFw.dint)) {
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
        return Val._NEW_INSTANCE_(dint, value);
    }

    public static BigInteger unwrap(Val dint) {
        if (dint.getType().equals(DIntFw.dint)) return unwrap0(dint);
        throw new IllegalArgumentException(dint.toString());
    }

    public static BigInteger unwrap0(Val dint) {
        return dint._UNPACK_();
    }

}
