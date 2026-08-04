package org.fw.core.memlib;

import org.fw.core.FW;
import org.fw.core.ast.Symbol;
import org.fw.core.base.Call;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.util.FwUtils;

import java.util.function.IntBinaryOperator;

public final class DWordFw {
    public static final Type dword = FW.telephonist((arg) -> {
        if (FwUtils.isTypeApiCall(arg, DWordFw.dword)) {
            Val instance = Call.getVal(arg);
            Val cArg = Call.getArg(arg);

            Integer value = unwrap(instance);
            assert value != null;
            if (cArg.type().equals(SymbolFw.symbol)) {
                String sym = cArg._unpack(Symbol.class).getValue();
                switch (sym) {
                    case "~": return wrap(~value);
                    case "|": return bop(instance, (a, b) -> a | b);
                    case "&": return bop(instance, (a, b) -> a & b);
                    case "^": return bop(instance, (a, b) -> a ^ b);
                    case "<<":
                    case "<<<":
                        return bop(instance, (a, b) -> a << b);
                    case "<<<<": return bop(instance, Integer::rotateLeft);
                    case ">>": return bop(instance, (a, b) -> a >> b);
                    case ">>>": return bop(instance, (a, b) -> a >>> b);
                    case ">>>>": return bop(instance, Integer::rotateRight);
                }
            }
        }
        return null;
    }).asType();

    private static Val bop(Val instance, IntBinaryOperator operator) {
        Integer value = unwrap(instance);
        return FW.telephonist((arg1) -> {
            if (arg1.type().equals(DWordFw.dword)) {
                Integer v2 = unwrap(arg1);
                return wrap(operator.applyAsInt(value, v2));
            }
            return null;
        });
    }

    public static Val wrap(int b) {
        return Val.of(dword, b);
    }

    public static Integer unwrap(Val val) {
        return val._unpack(Integer.class);
    }
}

