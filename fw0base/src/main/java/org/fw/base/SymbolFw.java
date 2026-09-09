package org.fw.base;

import org.fw.core.FW;

import static org.fw.core.FW.lambda_native;

public final class SymbolFw {
    public static final Type symbol = FW.lambda("Symbol", (arg) -> {
        return null; // ы
    }).asType();

    public static String unwrap(Val arg) {
        return arg._UNPACK_().toString();
    }

    // a perfect type, just as usual
}
