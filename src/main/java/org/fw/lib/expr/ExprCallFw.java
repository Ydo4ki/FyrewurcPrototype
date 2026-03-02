package org.fw.lib.expr;

import org.fw.FW;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.lib.DIntFw;
import org.fw.lib.VitFw;

import java.math.BigInteger;

import static org.fw.FW.symbol;

public final class ExprCallFw {
    public static final Val call = FW.telephonist("call", (arg, context) -> {
        if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"), context);
            Val cEnv = arg.call(symbol("comp-env"), context);
            int isize = size._unpack(BigInteger.class).intValue();
            if (isize == 0) {
                return Val.unspecified;
            }
            Val retVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(0), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(retVit.type()))
                return retVit; // compile error idk

            for (int i = 1; i < isize; i++) {
                Val argNVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(i), context)._unpack(), CompEnv.of(cEnv)), context);
                if (!VitFw.isVit(argNVit.type()))
                    return argNVit; // compile error idk

                retVit = VitFw.wrap(VitFw.unwrap(retVit).call(VitFw.unwrap(argNVit)));
            }
            return retVit;
        }
        return Val.unspecified;
    });
}
