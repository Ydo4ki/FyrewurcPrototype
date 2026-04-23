package org.fw.lib;

import org.fw.FW;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.lib.expr.ExprFw;

import static org.fw.FW.symbol;

public final class StrFw {
    public static final Type str = FW.telephonist("Str", (arg, context) -> {
        return Val.unspecified;
    }).asType();

    public static Val str(String string) {
        return Val.of(str, string);
    }
}
