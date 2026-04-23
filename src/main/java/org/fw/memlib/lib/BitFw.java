package org.fw.memlib.lib;

import org.fw.core.FW;
import org.fw.core.annotation.Insightful;
import org.fw.core.base.Type;
import org.fw.core.base.Val;

public final class BitFw {
    @Insightful
    public static final Type bit = FW.telephonist("bit", (arg, context) -> {
        return Val.unspecified;
    }).asType();

    @Insightful
    public static final Val bit0 = Val.of(bit, false);
    @Insightful
    public static final Val bit1 = Val.of(bit, true);
}
