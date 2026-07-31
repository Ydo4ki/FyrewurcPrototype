package org.fw.memlib.lib;

import org.fw.core.FW;
import org.fw.core.annotation.Insightful;
import org.fw.core.base.Type;
import org.fw.core.base.Val;

public final class ZWordFw {
    /* 64 bytes (512 bits) */
    public static final Type zword = FW.telephonist("zword", (arg, context) -> {
        return Val.unspecified;
    }).asType();
}
