package org.fw.core.memlib;

import org.fw.core.FW;
import org.fw.core.base.Type;
import org.fw.core.base.Val;

public final class BitFw {
    public static final Type bit = FW.telephonist(arg -> {
        return null;
    }).asType();

    public static final Val bit0 = Val.of(bit, false);
    public static final Val bit1 = Val.of(bit, true);
}
