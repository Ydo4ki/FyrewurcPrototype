package org.fw.lib;

import org.fw.FW;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.lib.expr.ExprFw;

import static org.fw.FW.symbol;

public final class StrFw {
    public static final Type str = FW.telephonist("Str", (arg, context) -> {
        if (arg.type().equals(ExprFw.toExpr)) {
            Val instance = BoxFw.unbox(arg);
            if (!instance.type().equals(StrFw.str))
                return Val.unspecified;

            return symbol('"' + instance._unpack(String.class) + '"');
//            return symbol(instance._unpack(String.class));
        }
        return Val.unspecified;
    }).asType();

    public static Val str(String string) {
        return Val.of(str, string);
    }
}
