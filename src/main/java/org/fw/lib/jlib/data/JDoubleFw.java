package org.fw.lib.jlib.data;

import org.fw.core.FW;
import org.fw.core.ast.Symbol;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.lib.elib.WrapperTypeFw;
import org.fw.lib.devicelib.PrimitiveLayoutsFw;

import java.util.function.DoubleBinaryOperator;

public final class JDoubleFw {
    public static final Type jdouble = WrapperTypeFw.wrapperType(PrimitiveLayoutsFw.dqword, FW.telephonist(instance -> FW.telephonist(rawPayload -> FW.telephonist(arg -> {
        Double value = unwrap(rawPayload);
        if (arg.type().equals(SymbolFw.symbol)) {
            String sym = arg._unpack(Symbol.class).getValue();
            switch (sym) {
                case "neg": return wrap(-value);
                case "+": return bop(value, Double::sum);
                case "-": return bop(value, (a, b) -> a - b);
                case "*": return bop(value, (a, b) -> a * b);
                case "/": return bop(value, (a, b) -> a / b);
                case "%": return bop(value, (a, b) -> a % b);
            }
        }
        return null;
    }))), FW.telephonist(arg -> null));

    private static Val bop(Double value, DoubleBinaryOperator operator) {
        return FW.telephonist((arg1) -> {
            if (arg1.type().equals(JDoubleFw.jdouble)) {
                Double v2 = unwrap(arg1);
                return wrap(operator.applyAsDouble(value, v2));
            }
            return null;
        });
    }

    public static Val wrap(Double b) {
        return Val.of(jdouble, Double.doubleToRawLongBits(b));
    }

    public static Double unwrap(Val val) {
        return Double.longBitsToDouble(val._unpack(Long.class));
    }
}

