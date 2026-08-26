package com.ydo4ki.fw.lib.memlib.ints;

import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.lib.stdlib.EnumFw;

public final class Endian {
    private Endian() {
    }

    public static final Type endian = EnumFw.enumeration("big", "little");

    public static final Val big = endian.get("big");
    public static final Val little = endian.get("little");
}
