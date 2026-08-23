package org.fw.lib.memlib.words;

import org.fw.core.FW;
import org.fw.core.base.Call;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.util.FwUtils;

import static org.fw.core.FW.symbol;

public final class BitFw {
    public static final Type bit = FW.telephonist(arg -> {
        if (FwUtils.isTypeApiCall(arg, BitFw.bit)) {
            Val instance = Call.getVal(arg);
            Val cArg = Call.getArg(arg);

            boolean value = instance._unpack(Boolean.class);
            if (cArg.equals(symbol("~"))) {
                return wrap(!value);
            } else if (cArg.equals(symbol("&"))) { // and
                return bop(value, (a, b) -> a && b);
            } else if (cArg.equals(symbol("|"))) { // or
                return bop(value, (a, b) -> a || b);
            } else if (cArg.equals(symbol("^"))) { // xor
                return bop(value, (a, b) -> a != b);
            } else if (cArg.equals(symbol("~&"))) { // nand
                return bop(value, (a, b) -> !(a && b));
            } else if (cArg.equals(symbol("~|"))) { // nor
                return bop(value, (a, b) -> !(a || b));
            } else if (cArg.equals(symbol("~^"))) { // xnor
                return bop(value, (a, b) -> a == b);
            }
        }
        return null;
    }).asType();

    interface BooleanBinaryOperator {
        boolean applyAsInt(boolean a, boolean b);
    }

    private static Val bop(boolean value, BooleanBinaryOperator operator) {
        return FW.telephonist((arg1) -> {
            if (arg1.type().equals(BitFw.bit)) {
                boolean v2 = arg1._unpack();
                return wrap(operator.applyAsInt(value, v2));
            }
            return null;
        });
    }

    public static Val wrap(boolean b) {
        return b ? bit1 : bit0;
    }

    public static final Val bit0 = Val.of(bit, false);
    public static final Val bit1 = Val.of(bit, true);
}
