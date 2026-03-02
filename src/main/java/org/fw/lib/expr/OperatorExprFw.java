package org.fw.lib.expr;

import org.fw.FwUtils;
import org.fw.ast.BracketsTypes;
import org.fw.ast.ExprList;
import org.fw.base.Call;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.lib.BoxFw;
import org.fw.lib.DIntFw;
import org.fw.lib.VitFw;
import org.fw.vit.Vit;

import java.math.BigInteger;

import static org.fw.FW.symbol;
import static org.fw.FW.telephonist;

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
            Vit argVit = VitFw.unwrap(argNVit);

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

            Vit v = Vit.val(OperatorExprFw.exprOperator.asVal()).call(symbol("constructor")).call(VitFw.unwrap(retVit));
            return VitFw.wrap(v);
        } else if (arg.equals(symbol("constructor"))) {
            return telephonist(OperatorExprFw.exprOperator.asVal().toExpr(context) + ".constructor", (argument, _) -> {
                return Val.of(OperatorExprFw.exprOperator, argument);
            });
        } else if (arg.type().equals(ExprFw.toExpr)) {
            Val instance = BoxFw.unbox(arg);
            if (!instance.type().equals(OperatorExprFw.exprOperator))
                return Val.unspecified;

            return ExprFw.wrap(ExprList.of(BracketsTypes.round,
                    OperatorExprFw.exprOperator.asVal().toExpr(context),
                    instance.call(symbol("operator"), context).toExpr(context)
            ));
        }
        return Val.unspecified;
    })).asType();
}
