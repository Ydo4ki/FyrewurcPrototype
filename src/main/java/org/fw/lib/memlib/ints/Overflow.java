package org.fw.lib.memlib.ints;

import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.lib.elib.EnumFw;

public final class Overflow {
    private Overflow() {
    }

    public static final Type overflow = EnumFw.enumeration("wrap", "saturate", "trap");

    public static final Val wrap = overflow.get("wrap");
    public static final Val saturate = overflow.get("saturate");
    public static final Val trap = overflow.get("trap");
}
