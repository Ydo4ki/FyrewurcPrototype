package org.fw.core.lib.expr;

import org.fw.core.FW;
import org.fw.core.base.Val;
import org.fw.core.lib.VitFw;
import org.fw.core.vit.Vit;

import java.math.BigInteger;

import static org.fw.core.FW.symbol;

public final class ExprVarFw {
    public static final Val var = FW.telephonist("var", (arg, context) -> {
        if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"), context);
            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 0) {
                return Val.unspecified;
            }
            return VitFw.wrap(Vit.var);
        }
        return Val.unspecified;
    });
}
