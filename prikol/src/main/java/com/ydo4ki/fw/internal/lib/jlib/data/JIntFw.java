package com.ydo4ki.fw.internal.lib.jlib.data;

import org.fw.core.FW;
import com.ydo4ki.esast.Symbol;
import org.fw.base.SymbolFw;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.esast.expr.ExprFw;
import org.fw.std.WrapperTypeFw;
import com.ydo4ki.fw.internal.lib.PrimitiveLayoutsFw;

import java.util.function.IntBinaryOperator;

public final class JIntFw {
    public static final Type jint = WrapperTypeFw.wrapperType(PrimitiveLayoutsFw.dword, FW.lambda_native(instance -> FW.lambda_native(rawPayload -> FW.lambda_native(arg -> {
        Integer value = unwrap(rawPayload);
        assert value != null;
        if (arg.getType().equals(SymbolFw.symbol)) {
            String sym = ((Symbol) ExprFw.unwrap(arg)).getValue();
            switch (sym) {
                case "neg": return wrap(-value);
                case "+": return bop(value, Integer::sum);
                case "-": return bop(value, (a, b) -> a - b);
                case "*": return bop(value, (a, b) -> a * b);
                case "/": return bop(value, (a, b) -> a / b);
                case "%": return bop(value, (a, b) -> a % b);
//                case "<=>": return bop(value, Integer::compare);

                case "~": return wrap(~value);
                case "|": return bop(value, (a, b) -> a | b);
                case "&": return bop(value, (a, b) -> a & b);
                case "^": return bop(value, (a, b) -> a ^ b);
                case "<<":
                case "<<<":
                    return bop(value, (a, b) -> a << b);
                case "<<<<": return bop(value, Integer::rotateLeft);
                case ">>": return bop(value, (a, b) -> a >> b);
                case ">>>": return bop(value, (a, b) -> a >>> b);
                case ">>>>": return bop(value, Integer::rotateRight);
            }
        }
        return null;
    }))), FW.lambda_native(arg -> null));

    private static Val bop(Integer value, IntBinaryOperator operator) {
        return FW.lambda_native((arg1) -> {
            if (arg1.getType().equals(JIntFw.jint)) {
                Integer v2 = unwrap(arg1);
                return wrap(operator.applyAsInt(value, v2));
            }
            return null;
        });
    }

    public static Val wrap(int b) {
        return Val._NEW_INSTANCE_(jint, b);
    }

    public static Integer unwrap(Val val) {
        return (Integer) val._UNPACK_();
    }
}

