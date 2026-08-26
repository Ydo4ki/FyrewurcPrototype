package org.fw.lib.stdlib.expr;

import org.fw.core.util.FwUtils;
import org.fw.core.base.CallFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.lib.stdlib.DIntFw;
import org.fw.lib.stdlib.VitFw;
import org.fw.core.vit.Vit;

import java.math.BigInteger;

import static org.fw.core.FW.*;

@Deprecated
public final class AccumulatorsExprFw {

    public static final Type exprAccumulator = telephonist("ExprAccumulator", ((arg) -> {
        if (FwUtils.isTypeApiCall(arg, AccumulatorsExprFw.exprAccumulator)) {
            Val instance = CallFw.getVal(arg);
            Val operator = instance._unpack();
            arg = CallFw.getArg(arg);
            if (arg.equals(symbol("operator"))) {
                return operator;
            }
            if (!arg.type().equals(ExprCallOpFw.exprCallOp)) {
                return null;
            }

            Val size = arg.call(symbol("size"));
            Val cEnv = arg.call(symbol("comp-env"));

            int isize = size._unpack(BigInteger.class).intValue();

            Vit ret = null;
            if (isize == 0)
                return null;

            for (int i = 0; i < isize; i++) {
                Val argNVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(i))._unpack(), CompEnv.of(cEnv)));
                if (!VitFw.isVit(argNVit.type()))
                    return argNVit; // compile error idk
                Vit argVit = VitFw.unwrap0(argNVit);

                if (ret == null) ret = argVit;
                else ret = ret.call(operator).call(argVit);
            }
            assert ret != null;
            return VitFw.wrap(ret);
        } else if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"));
            Val cEnv = arg.call(symbol("comp-env"));

            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 1)
                return null;

            Val retVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(0))._unpack(), CompEnv.of(cEnv)));
            if (!VitFw.isVit(retVit.type()))
                return retVit; // compile error idk

            Vit v = Vit.val(AccumulatorsExprFw.exprAccumulator.asVal()).call(symbol("constructor")).call(VitFw.unwrap0(retVit));
            return VitFw.wrap(v);
        } else if (arg.equals(symbol("constructor"))) {
            return telephonist((argument) -> {
                return Val.of(AccumulatorsExprFw.exprAccumulator, argument);
            });
        }
        return null;
    })).asType();
}
