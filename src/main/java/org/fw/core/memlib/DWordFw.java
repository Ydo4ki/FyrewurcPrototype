package org.fw.core.memlib;

import org.fw.core.FW;
import org.fw.core.ast.Symbol;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.WrapperTypeFw;

import java.util.function.IntBinaryOperator;

public final class DWordFw {
    public static final Type dword_raw = ReifiedTypeFw.reifiedType(BitFw.bit, 32);
    public static final Type dword = WrapperTypeFw.wrapperType(dword_raw, FW.telephonist(rawPayload -> FW.telephonist(arg -> {
        Integer value = unwrap(rawPayload);
        assert value != null;
        if (arg.type().equals(SymbolFw.symbol)) {
            String sym = arg._unpack(Symbol.class).getValue();
            switch (sym) {
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

