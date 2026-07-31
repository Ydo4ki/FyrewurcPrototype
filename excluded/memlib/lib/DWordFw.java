package org.fw.memlib.lib;

import org.fw.core.FW;
import org.fw.core.annotation.Insightful;
import org.fw.core.base.Type;
import org.fw.core.base.Val;

public final class DWordFw {
    public static final Type dword = FW.telephonist("dword", (arg, context) -> {
        return Val.unspecified;
    }).asType();

    public static Val wrap(int b) {
        return Val.of(dword, b);
    }

    public static Integer unwrap(Val val) {
        return val._unpack();
    }
}

