package org.fw.lib.expr;

import org.fw.FW;
import org.fw.base.Val;
import org.fw.lib.DIntFw;
import org.fw.lib.VitFw;
import org.fw.vit.Vit;

import java.math.BigInteger;

import static org.fw.FW.symbol;

public final class ExprGetFw {
    public static final Val get = FW.telephonist("get", (arg, context) -> {
        if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"), context);
            Val cEnv = arg.call(symbol("comp-env"), context);
            int isize = size._unpack(BigInteger.class).intValue();

            if (isize == 0)
                return Val.unspecified;

            Val retVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(0), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(retVit.type()))
                return retVit; // compile error idk

//            System.out.println("0: " + ret);
            for (int i = 1; i < isize; i++) {
                Val property = arg.call(DIntFw.dint(i), context);
                if (!property.type().equals(ExprFw.symbol))
                    return Val.unspecified; // not a compile error idk (actually it still is)

                retVit = VitFw.wrap(VitFw.unwrap(retVit).call(Vit.val(property)));
                // ok I''m actually not sure if that's gonna work
            }
            // nice
            return retVit;
        }
        return Val.unspecified;
    });
}
