package org.fw.memlib.lib;

import org.fw.core.FW;
import org.fw.core.annotation.Insightful;
import org.fw.core.base.Type;
import org.fw.core.base.Val;

public final class TWordFw {
    /* 10 bytes (80 bits) */
    public static final Type tword = FW.telephonist("tword", (arg, context) -> {
        return Val.unspecified;
    }).asType();
}

