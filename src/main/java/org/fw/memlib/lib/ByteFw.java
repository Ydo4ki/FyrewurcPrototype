package org.fw.memlib.lib;

import org.fw.core.FW;
import org.fw.core.base.Type;
import org.fw.core.base.Val;

public final class ByteFw {
    public static final Type $byte = FW.telephonist("byte", (arg, context) -> {
        return Val.unspecified;
    }).asType();

    private static final Val[] cache = new Val[256];

    static {
        for (int i = 0; i < 256; i++) {
            cache[i] = Val.of($byte, (byte) i);
        }
    }

    public static Val wrap(byte b) {
        return cache[Byte.toUnsignedInt(b)];
    }

    public static Byte unwrap(Val val) {
        return val._unpack();
    }
}
