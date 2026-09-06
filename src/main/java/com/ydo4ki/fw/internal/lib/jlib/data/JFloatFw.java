package com.ydo4ki.fw.internal.lib.jlib.data;

import org.fw.core.FW;
import org.fw.core.ast.Symbol;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.lib.stdlib.WrapperTypeFw;
import com.ydo4ki.fw.internal.lib.devicelib.PrimitiveLayoutsFw;

public final class JFloatFw {
    public static final Type jfloat = WrapperTypeFw.wrapperType(PrimitiveLayoutsFw.dword, FW.telephonist_native(instance -> FW.telephonist_native(rawPayload -> FW.telephonist_native(arg -> {
        Float value = unwrap(rawPayload);
        if (arg.getType().equals(SymbolFw.symbol)) {
            String sym = arg._UNPACK(Symbol.class).getValue();
            switch (sym) {
                case "neg": return wrap(-value);
                case "+": return bop(value, Float::sum);
                case "-": return bop(value, (a, b) -> a - b);
                case "*": return bop(value, (a, b) -> a * b);
                case "/": return bop(value, (a, b) -> a / b);
                case "%": return bop(value, (a, b) -> a % b);
            }
        }
        return null;
    }))), FW.telephonist_native(arg -> null));

    private static Val bop(Float value, FloatBinaryOperator operator) {
        return FW.telephonist_native((arg1) -> {
            if (arg1.getType().equals(JFloatFw.jfloat)) {
                Float v2 = unwrap(arg1);
                return wrap(operator.applyAsFloat(value, v2));
            }
            return null;
        });
    }

    interface FloatBinaryOperator {
        float applyAsFloat(float left, float right);
    }

    public static Val wrap(float b) {
        return Val.of(jfloat, Float.floatToRawIntBits(b));
    }

    public static Float unwrap(Val val) {
        return Float.intBitsToFloat(val._UNPACK(Integer.class));
    }
}

