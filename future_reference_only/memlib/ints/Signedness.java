package com.ydo4ki.fw.internal.lib.memlib.ints;

import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.std.EnumFw;

public final class Signedness {
    private Signedness() {
    }

    public static final Type signedness = EnumFw.enumeration("signed", "unsigned");

    public static final Val signed = (Val) signedness.get("signed");
    public static final Val unsigned = (Val) signedness.get("unsigned");
}
