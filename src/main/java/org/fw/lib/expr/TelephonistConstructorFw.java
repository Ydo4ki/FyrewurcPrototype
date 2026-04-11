package org.fw.lib.expr;

import org.fw.FW;
import org.fw.base.Val;
import org.fw.lib.DIntFw;
import org.fw.lib.VitFw;
import org.fw.lib.telephonist.VitiateTelephonistFw;
import org.fw.vit.Vit;

import java.math.BigInteger;

import static org.fw.FW.symbol;

public final class TelephonistConstructorFw {
    public static final Val telephonist = FW.telephonist("telephonist", (arg, context) -> {
        if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"), context);
            Val cEnv = arg.call(symbol("comp-env"), context);
            int isize = size._unpack(BigInteger.class).intValue();

            if (isize != 2)
                return Val.unspecified;

            Val varKey = arg.call(DIntFw.dint(0), context);
            if (!varKey.type().equals(ExprFw.symbol)) {
                return Val.unspecified;
            }

            Val argCEnv = FW.telephonist("arg-comp-env", (arg1, context1) -> {
                if (arg1.type().equals(SyntaxResolveFw.syntaxResolve)) {
                    Val exprVal = arg1.call(symbol("expr"), context1);
                    if (exprVal.equals(varKey))
                        return VitFw.wrap(Vit.var(varKey));
                }
                return Val.unspecified;
            });

            cEnv = CompEnv.compEnv(context, cEnv, argCEnv);

            Val bodyVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(bodyVit.type()))
                return bodyVit; // error probably

            bodyVit = VitFw.wrap(Vit.simplify(VitFw.unwrap(bodyVit), context));

            return VitFw.wrap(Vit.val(VitiateTelephonistFw.vitiateTelephonist.asVal()).call(symbol("builder")).call(bodyVit).call(varKey).call(Vit.var));
        }
        return Val.unspecified;
    });
}
