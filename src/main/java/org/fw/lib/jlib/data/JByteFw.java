package org.fw.lib.jlib.data;

import org.fw.core.FW;
import org.fw.core.ast.Symbol;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.lib.elib.WrapperTypeFw;
import org.fw.lib.devicelib.PrimitiveLayoutsFw;

public final class JByteFw {
    public static final Type jbyte = WrapperTypeFw.wrapperType(PrimitiveLayoutsFw.octet, FW.telephonist(instance -> FW.telephonist(rawPayload -> FW.telephonist(arg -> {
        Byte value = unwrap(rawPayload);
        if (arg.type().equals(SymbolFw.symbol)) {
            String sym = arg._unpack(Symbol.class).getValue();
            switch (sym) {
                case "neg": return wrap((byte) -value);
                case "+": return bopBBB(value, (a, b) -> (byte) (a + b));
                case "-": return bopBBB(value, (a, b) -> (byte) (a - b));
                case "*": return bopBBB(value, (a, b) -> (byte) (a * b));
                case "/": return bopBBB(value, (a, b) -> (byte) (a / b));
                case "%": return bopBBB(value, (a, b) -> (byte) (a % b));

                case "~": return wrap((byte) ~value);
                case "|": return bopBBB(value, (a, b) -> (byte) (a | b));
                case "&": return bopBBB(value, (a, b) -> (byte) (a & b));
                case "^": return bopBBB(value, (a, b) -> (byte) (a ^ b));
                case "<<":
                case "<<<":
                    return bopBIB(value, (a, b) -> (byte) (a << b));
                case "<<<<": return bopBIB(value, JByteFw::rotateLeft);
                case ">>": return bopBIB(value, (a, b) -> (byte) (a >> b));
                case ">>>": return bopBIB(value, (a, b) -> (byte) (a >>> b));
                case ">>>>": return bopBIB(value, JByteFw::rotateRight);
            }
        }
        return null;
    }))), FW.telephonist(arg -> null));

    public static byte rotateRight(byte value, int distance) {
        distance = distance & 7;
        int unsignedValue = value & 0xFF;
        return (byte) ((unsignedValue >>> distance) | (unsignedValue << (8 - distance)));
    }

    public static byte rotateLeft(byte value, int distance) {
        distance = distance & 7;
        return (byte) (((value << distance) | ((value & 0xFFFF) >>> (8 - distance))) & 0xFF);
    }



    private static Val bopBIB(Byte value, ByteIntByteOperator operator) {
        return FW.telephonist((arg1) -> {
            if (arg1.type().equals(JIntFw.jint)) {
                Integer v2 = JIntFw.unwrap(arg1);
                return wrap(operator.apply(value, v2));
            }
            return null;
        });
    }

    interface ByteIntByteOperator {
        byte apply(byte left, int right);
    }

    interface ByteBinaryOperator {
        byte applyAsShort(byte left, byte right);
    }

    private static Val bopBBB(Byte value, ByteBinaryOperator operator) {
        return FW.telephonist((arg1) -> {
            if (arg1.type().equals(JByteFw.jbyte)) {
                Byte v2 = unwrap(arg1);
                return wrap(operator.applyAsShort(value, v2));
            }
            return null;
        });
    }

    public static Val wrap(Byte b) {
        return Val.of(jbyte, b);
    }

    public static Byte unwrap(Val val) {
        return val._unpack(Byte.class);
    }
}

