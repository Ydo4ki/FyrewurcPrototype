package org.fw.lib.expr;

import org.fw.FW;
import org.fw.base.Val;
import org.fw.lib.VitFw;
import org.fw.vit.Vit;

import java.math.BigInteger;

import static org.fw.FW.symbol;

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
