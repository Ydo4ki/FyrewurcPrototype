package org.fw.core.jlib.data;

import org.fw.core.FW;
import org.fw.core.ast.Symbol;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.WrapperTypeFw;
import org.fw.core.memlib.words.PrimitiveLayoutsFw;

import java.util.function.IntBinaryOperator;

public final class JIntFw {
    public static final Type jint = WrapperTypeFw.wrapperType(PrimitiveLayoutsFw.dword, FW.telephonist(rawPayload -> FW.telephonist(arg -> {
        Integer value = unwrap(rawPayload);
        assert value != null;
        if (arg.type().equals(SymbolFw.symbol)) {
            String sym = arg._unpack(Symbol.class).getValue();
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
    })), FW.telephonist(arg -> null));

    private static Val bop(Integer value, IntBinaryOperator operator) {
        return FW.telephonist((arg1) -> {
            if (arg1.type().equals(JIntFw.jint)) {
                Integer v2 = unwrap(arg1);
                return wrap(operator.applyAsInt(value, v2));
            }
            return null;
        });
    }

    public static Val wrap(int b) {
        return Val.of(jint, b);
    }

    public static Integer unwrap(Val val) {
        return val._unpack(Integer.class);
    }
}

