package org.fw.base;

import org.fw.core.FW;

import static org.fw.core.FW.telephonist_native;

public final class SymbolFw {
    public static final Type symbol = FW.telephonist("Symbol", (arg) -> {
        return null; // ы
    }).asType();

    // a perfect type, just as usual
}
