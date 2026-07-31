package org.fw.core.base;

import static org.fw.core.FW.telephonist;

public final class SymbolFw {
    public static final Type symbol = telephonist("Symbol", (arg) -> {
        return null; // ы
    }).asType();

    // a perfect type, just as usual
}
