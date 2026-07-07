package org.fw.core.lib.expr;

import org.fw.core.FW;
import org.fw.core.util.FwUtils;
import org.fw.core.base.Call;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.DIntFw;
import org.fw.core.lib.VitFw;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;

import java.math.BigInteger;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

public final class OperatorExprFw {

    public static final Type exprOperator = telephonist("ExprOperator", ((arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, OperatorExprFw.exprOperator, context)) {
            Val instance = Call.getVal(arg, context);
            Val operator = instance._unpack();
            arg = Call.getArg(arg, context);
            if (arg.equals(symbol("operator"))) {
                return operator;
            }
            if (!arg.type().equals(ExprCallOpFw.exprCallOp)) {
                return Val.unspecified;
            }

            Val size = arg.call(symbol("size"), context);
            Val cEnv = arg.call(symbol("comp-env"), context);

            int isize = size._unpack(BigInteger.class).intValue();

            if (isize != 1)
                return Val.unspecified;

            Val argNVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(0), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(argNVit.type()))
                return argNVit; // compile error idk
            Vit argVit = null;
            try {
                argVit = VitFw.unwrap(argNVit);
            } catch (VitCompilationException e) {
                throw new RuntimeException(e);
            }

            assert argVit != null;
            return VitFw.wrap(Vit.val(operator).call(argVit));
        } else if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"), context);
            Val cEnv = arg.call(symbol("comp-env"), context);

            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 1)
                return Val.unspecified;

            Val retVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(0), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(retVit.type()))
                return retVit; // compile error idk

            Vit v = null;
            try {
                v = Vit.val(OperatorExprFw.exprOperator.asVal()).call(FW.symbol("constructor")).call(VitFw.unwrap(retVit));
            } catch (VitCompilationException e) {
                throw new RuntimeException(e);
            }
            return VitFw.wrap(v);
        } else if (arg.equals(symbol("constructor"))) {
            return telephonist(OperatorExprFw.exprOperator.asVal().toExpr(context) + ".constructor", (argument, c) -> {
                return Val.of(OperatorExprFw.exprOperator, argument);
            });
        }
        return Val.unspecified;
    })).asType();
}
