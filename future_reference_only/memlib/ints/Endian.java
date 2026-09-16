package com.ydo4ki.fw.internal.lib.memlib.ints;

import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.std.EnumFw;

public final class Endian {
    private Endian() {
    }

    public static final Type endian = EnumFw.enumeration("big", "little");

    public static final Val big = (Val) endian.get("big");
    public static final Val little = (Val) endian.get("little");
}
