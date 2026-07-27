package org.fw.core.lib.expr;

import org.fw.core.base.Unspecified;
import org.fw.core.util.FwUtils;
import org.fw.core.base.Call;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.DIntFw;
import org.fw.core.lib.VitFw;
import org.fw.core.vit.Vit;

import java.math.BigInteger;

import static org.fw.core.FW.*;

public final class AccumulatorsExprFw {

    public static final Type exprAccumulator = telephonist("ExprAccumulator", ((arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, AccumulatorsExprFw.exprAccumulator, context)) {
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

            Vit ret = null;
            if (isize == 0)
                return null;

            for (int i = 0; i < isize; i++) {
                Val argNVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(i), context)._unpack(), CompEnv.of(cEnv)), context);
                if (!VitFw.isVit(argNVit.type()))
                    return argNVit; // compile error idk
                Vit argVit = VitFw.unwrap0(argNVit);

                if (ret == null) ret = argVit;
                else ret = ret.call(operator).call(argVit);
            }
            assert ret != null;
            return VitFw.wrap(ret);
        } else if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"), context);
            Val cEnv = arg.call(symbol("comp-env"), context);

            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 1)
                return null;

            Val retVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(0), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(retVit.type()))
                return retVit; // compile error idk

            Vit v = Vit.val(AccumulatorsExprFw.exprAccumulator.asVal()).call(symbol("constructor")).call(VitFw.unwrap0(retVit));
            return VitFw.wrap(v);
        } else if (arg.equals(symbol("constructor"))) {
            return telephonist(AccumulatorsExprFw.exprAccumulator.asVal().toExpr(context) + ".constructor", (argument, c) -> {
                return Val.of(AccumulatorsExprFw.exprAccumulator, argument);
            });
        }
        return null;
    })).asType();
}
