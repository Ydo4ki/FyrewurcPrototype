package com.ydo4ki.fw.internal.lib.jlib.data;

import org.fw.core.FW;
import com.ydo4ki.esast.Symbol;
import org.fw.base.SymbolFw;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.esast.expr.ExprFw;
import org.fw.std.WrapperTypeFw;
import com.ydo4ki.fw.internal.lib.PrimitiveLayoutsFw;

import java.util.function.LongBinaryOperator;

public final class JLongFw {
    public static final Type jlong = WrapperTypeFw.wrapperType(PrimitiveLayoutsFw.dqword, FW.lambda_native(instance -> FW.lambda_native(rawPayload -> FW.lambda_native(arg -> {
        Long value = unwrap(rawPayload);
        if (arg.getType().equals(SymbolFw.symbol)) {
            String sym = ((Symbol) ExprFw.unwrap(arg)).getValue();
            switch (sym) {
                case "neg": return wrap(-value);
                case "+": return bopLLL(value, Long::sum);
                case "-": return bopLLL(value, (a, b) -> a - b);
                case "*": return bopLLL(value, (a, b) -> a * b);
                case "/": return bopLLL(value, (a, b) -> a / b);
                case "%": return bopLLL(value, (a, b) -> a % b);

                case "~": return wrap(~value);
                case "|": return bopLLL(value, (a, b) -> a | b);
                case "&": return bopLLL(value, (a, b) -> a & b);
                case "^": return bopLLL(value, (a, b) -> a ^ b);
                case "<<":
                case "<<<":
                    return bopLIL(value, (a, b) -> a << b);
                case "<<<<": return bopLIL(value, Long::rotateLeft);
                case ">>": return bopLIL(value, (a, b) -> a >> b);
                case ">>>": return bopLIL(value, (a, b) -> a >>> b);
                case ">>>>": return bopLIL(value, Long::rotateRight);
            }
        }
        return null;
    }))), FW.lambda_native(arg -> null));

    private static Val bopLIL(Long value, LongIntLongOperator operator) {
        return FW.lambda_native((arg1) -> {
            if (arg1.getType().equals(JIntFw.jint)) {
                Integer v2 = JIntFw.unwrap(arg1);
                return wrap(operator.apply(value, v2));
            }
            return null;
        });
    }

    interface LongIntLongOperator {
        long apply(long left, int right);
    }

    private static Val bopLLL(Long value, LongBinaryOperator operator) {
        return FW.lambda_native((arg1) -> {
            if (arg1.getType().equals(JLongFw.jlong)) {
                Long v2 = unwrap(arg1);
                return wrap(operator.applyAsLong(value, v2));
            }
            return null;
        });
    }

    public static Val wrap(Long b) {
        return Val._NEW_INSTANCE_(jlong, b);
    }

    public static Long unwrap(Val val) {
        return val._UNPACK_(Long.class);
    }
}

