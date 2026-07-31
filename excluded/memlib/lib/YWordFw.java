package org.fw.memlib.lib;

import org.fw.core.FW;
import org.fw.core.annotation.Insightful;
import org.fw.core.base.Type;
import org.fw.core.base.Val;

public final class YWordFw {
    /* 32 bytes (256 bits) */
    public static final Type yword = FW.telephonist("yword", (arg, context) -> {
        return Val.unspecified;
    }).asType();
}

