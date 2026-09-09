package com.ydo4ki.fw.internal.lib.jlib.data;

import org.fw.core.FW;
import com.ydo4ki.esast.Symbol;
import org.fw.base.SymbolFw;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.esast.expr.ExprFw;
import org.fw.std.WrapperTypeFw;
import com.ydo4ki.fw.internal.lib.PrimitiveLayoutsFw;

import java.util.function.DoubleBinaryOperator;

public final class JDoubleFw {
    public static final Type jdouble = WrapperTypeFw.wrapperType(PrimitiveLayoutsFw.dqword, FW.lambda_native(instance -> FW.lambda_native(rawPayload -> FW.lambda_native(arg -> {
        Double value = unwrap(rawPayload);
        if (arg.getType().equals(SymbolFw.symbol)) {
            String sym = ((Symbol) ExprFw.unwrap(arg)).getValue();
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
    }))), FW.lambda_native(arg -> null));

    private static Val bop(Double value, DoubleBinaryOperator operator) {
        return FW.lambda_native((arg1) -> {
            if (arg1.getType().equals(JDoubleFw.jdouble)) {
                Double v2 = unwrap(arg1);
                return wrap(operator.applyAsDouble(value, v2));
            }
            return null;
        });
    }

    public static Val wrap(Double b) {
        return Val._NEW_INSTANCE_(jdouble, Double.doubleToRawLongBits(b));
    }

    public static Double unwrap(Val val) {
        return Double.longBitsToDouble(val._UNPACK_());
    }
}

