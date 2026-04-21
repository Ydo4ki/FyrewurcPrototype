package org.fw.memlib;

import org.fw.FW;
import org.fw.annotation.Insightful;
import org.fw.base.Type;
import org.fw.base.Val;

public final class Bit {
    @Insightful
    public static final Type bit = FW.telephonist("bit", (arg, context) -> {
        return Val.unspecified;
    }).asType();

    @Insightful
    public static final Val bit0 = Val.of(bit, false);
    @Insightful
    public static final Val bit1 = Val.of(bit, true);
}
