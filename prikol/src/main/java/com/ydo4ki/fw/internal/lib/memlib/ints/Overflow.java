package com.ydo4ki.fw.internal.lib.memlib.ints;

import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.std.EnumFw;

public final class Overflow {
    private Overflow() {
    }

    public static final Type overflow = EnumFw.enumeration("wrap", "saturate", "trap");

    public static final Val wrap = (Val) overflow.get("wrap");
    public static final Val saturate = (Val) overflow.get("saturate");
    public static final Val trap = (Val) overflow.get("trap");
}
