package org.fw.base;

import org.fw.core.FW;

import static org.fw.core.FW.lambda_native;

public final class SymbolFw {
    private static final MagicPowers magic = MagicPowers.getMagicPowers();

    public static final Type symbol = FW.lambda("Symbol", (arg) -> {
        return null; // ы
    }).asType();

    public static String unwrap(Val arg) {
        return magic.unpackVal(arg).toString();
    }

    // a perfect type, just as usual
}
