package org.fw.core.memlib;

import org.fw.core.FW;
import org.fw.core.base.Type;
import org.fw.core.base.Val;

public final class DWordFw {
    public static final Type dword = FW.telephonist((arg) -> {
        return null;
    }).asType();

    public static Val wrap(int b) {
        return Val.of(dword, b);
    }

    public static Integer unwrap(Val val) {
        return val._unpack(Integer.class);
    }
}

