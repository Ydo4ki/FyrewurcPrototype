package org.fw.lib.jlib.data;

import org.fw.core.FW;
import org.fw.core.ast.Symbol;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.lib.elib.WrapperTypeFw;
import org.fw.lib.memlib.words.PrimitiveLayoutsFw;

public final class JCharFw {
    public static final Type jchar = WrapperTypeFw.wrapperType(PrimitiveLayoutsFw.word, FW.telephonist(instance -> FW.telephonist(rawPayload -> FW.telephonist(arg -> {
        Short value = unwrap(rawPayload);
        if (arg.type().equals(SymbolFw.symbol)) {
            String sym = arg._unpack(Symbol.class).getValue();
            switch (sym) {
                case "neg": return wrap((short) -value);
                case "+": return bopSSS(value, (a, b) -> (short) (a + b));
                case "-": return bopSSS(value, (a, b) -> (short) (a - b));
                case "*": return bopSSS(value, (a, b) -> (short) (a * b));
                case "/": return bopSSS(value, (a, b) -> (short) (a / b));
                case "%": return bopSSS(value, (a, b) -> (short) (a % b));

                case "~": return wrap((short) ~value);
                case "|": return bopSSS(value, (a, b) -> (short) (a | b));
                case "&": return bopSSS(value, (a, b) -> (short) (a & b));
                case "^": return bopSSS(value, (a, b) -> (short) (a ^ b));
                case "<<":
                case "<<<":
                    return bopSIS(value, (a, b) -> (short) (a << b));
                case "<<<<": return bopSIS(value, JCharFw::rotateLeft);
                case ">>": return bopSIS(value, (a, b) -> (short) (a >> b));
                case ">>>": return bopSIS(value, (a, b) -> (short) (a >>> b));
                case ">>>>": return bopSIS(value, JCharFw::rotateRight);
            }
        }
        return null;
    }))), FW.telephonist(arg -> null));

    public static short rotateRight(short value, int distance) {
        distance = distance & 15;
        int unsignedValue = value & 0xFFFF;
        return (short) ((unsignedValue >>> distance) | (unsignedValue << (16 - distance)));
    }

    public static short rotateLeft(short value, int distance) {
        distance = distance & 15;
        return (short) (((value << distance) | ((value & 0xFFFF) >>> (16 - distance))) & 0xFFFF);
    }



    private static Val bopSIS(Short value, ShortIntShortOperator operator) {
        return FW.telephonist((arg1) -> {
            if (arg1.type().equals(JIntFw.jint)) {
                Integer v2 = JIntFw.unwrap(arg1);
                return wrap(operator.apply(value, v2));
            }
            return null;
        });
    }

    interface ShortIntShortOperator {
        short apply(short left, int right);
    }

    interface ShortBinaryOperator {
        short applyAsShort(short left, short right);
    }

    private static Val bopSSS(Short value, ShortBinaryOperator operator) {
        return FW.telephonist((arg1) -> {
            if (arg1.type().equals(JCharFw.jchar)) {
                Short v2 = unwrap(arg1);
                return wrap(operator.applyAsShort(value, v2));
            }
            return null;
        });
    }

    public static Val wrap(Short b) {
        return Val.of(jchar, b);
    }

    public static Val wrap(Character b) {
        return Val.of(jchar, (short)b.charValue());
    }

    public static Short unwrap(Val val) {
        return val._unpack(Short.class);
    }
}

