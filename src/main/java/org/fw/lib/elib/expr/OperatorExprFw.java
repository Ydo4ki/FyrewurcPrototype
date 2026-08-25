package org.fw.lib.elib.expr;

import org.fw.core.FW;
import org.fw.core.ast.Expr;
import org.fw.core.util.FwUtils;
import org.fw.core.base.Call;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.lib.elib.DIntFw;
import org.fw.lib.elib.VitFw;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;

import java.math.BigInteger;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

@Deprecated
public final class OperatorExprFw {

    public static final Type exprOperator = FW.telephonist("ExprOperator", ((arg) -> {
        if (FwUtils.isTypeApiCall(arg, OperatorExprFw.exprOperator)) {
            Val instance = Call.getVal(arg);
            Val operator = instance._unpack();
            arg = Call.getArg(arg);
            if (arg.equals(symbol("operator"))) {
                return operator;
            }
            if (!arg.type().equals(ExprCallOpFw.exprCallOp)) {
                return null;
            }

            Val size = arg.call(symbol("size"));
            Val cEnv = arg.call(symbol("comp-env"));

            int isize = size._unpack(BigInteger.class).intValue();

            if (isize != 1)
                return null;

            Expr eee = arg.call(DIntFw.dint(0))._unpack();
            Val argNVit = cEnv.call(CompEnv.syntaxResolve(eee, CompEnv.of(cEnv)));
            if (!VitFw.isVit(argNVit.type()))
                return argNVit; // compile error idk
            Vit argVit = null;
            try {
                argVit = VitFw.unwrap(argNVit, eee);
            } catch (VitCompilationException e) {
                throw new RuntimeException(e);
            }

            assert argVit != null;
            return VitFw.wrap(Vit.val(operator).call(argVit));
        } else if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"));
            Val cEnv = arg.call(symbol("comp-env"));

            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 1)
                return null;

            Expr eee = arg.call(DIntFw.dint(0))._unpack();
            Val retVit = cEnv.call(CompEnv.syntaxResolve(eee, CompEnv.of(cEnv)));
            if (!VitFw.isVit(retVit.type()))
                return retVit; // compile error idk

            Vit v = null;
            try {
                v = Vit.val(OperatorExprFw.exprOperator.asVal()).call(FW.symbol("constructor")).call(VitFw.unwrap(retVit, eee));
            } catch (VitCompilationException e) {
                throw new RuntimeException(e);
            }
            return VitFw.wrap(v);
        } else if (arg.equals(symbol("constructor"))) {
            return FW.telephonist((argument) -> {
                return Val.of(OperatorExprFw.exprOperator, argument);
            });
        }
        return null;
    })).asType();
}
