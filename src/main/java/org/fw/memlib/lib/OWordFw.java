package org.fw.memlib.lib;

import org.fw.core.FW;
import org.fw.core.annotation.Insightful;
import org.fw.core.base.Type;
import org.fw.core.base.Val;

public final class OWordFw {
    /* 16 bytes (128 bits) */
    @Insightful
    public static final Type oword = FW.telephonist("oword", (arg, context) -> {
        return Val.unspecified;
    }).asType();
}

