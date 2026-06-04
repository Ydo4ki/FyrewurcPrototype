package org.fw.memlib.lib;

import org.fw.core.FW;
import org.fw.core.annotation.Insightful;
import org.fw.core.base.Type;
import org.fw.core.base.Val;

public final class QWordFw {
    @Insightful
    public static final Type qword = FW.telephonist("qword", (arg, context) -> {
        return Val.unspecified;
    }).asType();

    public static Val wrap(long b) {
        return Val.of(qword, b);
    }

    public static Long unwrap(Val val) {
        return val._unpack();
    }
}
