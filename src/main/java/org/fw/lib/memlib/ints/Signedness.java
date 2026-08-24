package org.fw.lib.memlib.ints;

import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.lib.elib.EnumFw;

public final class Signedness {
    private Signedness() {
    }

    public static final Type signedness = EnumFw.enumeration("signed", "unsigned");

    public static final Val signed = signedness.get("signed");
    public static final Val unsigned = signedness.get("unsigned");
}
