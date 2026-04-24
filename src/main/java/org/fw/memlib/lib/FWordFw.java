package org.fw.memlib.lib;

import org.fw.core.FW;
import org.fw.core.annotation.Insightful;
import org.fw.core.base.Type;
import org.fw.core.base.Val;

public final class FWordFw {
    /* 6 bytes (48 bits) */
    @Insightful
    public static final Type fword = FW.telephonist("fword", (arg, context) -> {
        return Val.unspecified;
    }).asType();
}


