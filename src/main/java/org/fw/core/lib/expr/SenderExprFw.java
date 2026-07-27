package org.fw.core.lib.expr;

import org.fw.core.base.Unspecified;
import org.fw.core.util.FwUtils;
import org.fw.core.annotation.Insightful;
import org.fw.core.base.Call;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.DIntFw;
import org.fw.core.lib.VitFw;
import org.fw.core.vit.Vit;

import java.math.BigInteger;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

public final class SenderExprFw {

    @Insightful
    public static final Type exprSender = telephonist("ExprSender", ((arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, SenderExprFw.exprSender, context)) {
            Val instance = Call.getVal(arg, context);
            Val operator = instance._unpack();
            arg = Call.getArg(arg, context);
            if (arg.equals(symbol("operator"))) {
                return operator;
            }
            if (!arg.type().equals(ExprCallOpFw.exprCallOp)) {
                return null;
            }

            Val size = arg.call(symbol("size"), context);
            Val cEnv = arg.call(symbol("comp-env"), context);

            int isize = size._unpack(BigInteger.class).intValue();

            if (isize != 1)
                return null;

            Val argNVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(0), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(argNVit.type()))
                return argNVit; // compile error idk
            Vit argVit = VitFw.unwrap0(argNVit);

            assert argVit != null;
            return VitFw.wrap(argVit.call(operator));
        } else if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"), context);
            Val cEnv = arg.call(symbol("comp-env"), context);

            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 1)
                return null;

            Val retVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(0), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(retVit.type()))
                return retVit; // compile error idk

            Vit v = Vit.val(SenderExprFw.exprSender.asVal()).call(symbol("constructor")).call(VitFw.unwrap0(retVit));
            return VitFw.wrap(v);
        } else if (arg.equals(symbol("constructor"))) {
            return telephonist(SenderExprFw.exprSender.asVal().toExpr(context) + ".constructor", (argument, c) -> {
                return Val.of(SenderExprFw.exprSender, argument);
            });
        }
        return null;
    })).asType();
}
