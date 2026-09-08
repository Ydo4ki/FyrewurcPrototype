package com.ydo4ki.fw.internal.lib.jlib.data;

import org.fw.core.FW;
import com.ydo4ki.esast.Symbol;
import org.fw.base.SymbolFw;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.esast.expr.ExprFw;
import org.fw.std.WrapperTypeFw;
import com.ydo4ki.fw.internal.lib.PrimitiveLayoutsFw;

public final class JFloatFw {
    public static final Type jfloat = WrapperTypeFw.wrapperType(PrimitiveLayoutsFw.dword, FW.lambda_native(instance -> FW.lambda_native(rawPayload -> FW.lambda_native(arg -> {
        Float value = unwrap(rawPayload);
        if (arg.getType().equals(SymbolFw.symbol)) {
            String sym = ((Symbol) ExprFw.unwrap(arg)).getValue();
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
    }))), FW.lambda_native(arg -> null));

    private static Val bop(Float value, FloatBinaryOperator operator) {
        return FW.lambda_native((arg1) -> {
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
        return Val._NEW_INSTANCE_(jfloat, Float.floatToRawIntBits(b));
    }

    public static Float unwrap(Val val) {
        return Float.intBitsToFloat(val._UNPACK_(Integer.class));
    }
}

