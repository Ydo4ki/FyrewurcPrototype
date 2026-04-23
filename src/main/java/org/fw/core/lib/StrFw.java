package org.fw.core.lib;

import org.fw.core.FW;
import org.fw.core.base.Type;
import org.fw.core.base.Val;

public final class StrFw {
    public static final Type str = FW.telephonist("Str", (arg, context) -> {
        return Val.unspecified;
    }).asType();

    public static Val str(String string) {
        return Val.of(str, string);
    }
}
