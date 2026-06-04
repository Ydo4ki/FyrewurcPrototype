package org.fw.core.lib.comp;

import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.base.Val;
import org.fw.core.lib.VitFw;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.ExprCallOpFw;
import org.fw.core.lib.expr.ExprFw;
import org.fw.core.lib.expr.SyntaxResolveFw;
import org.fw.core.state.operation.Operation;
import org.fw.core.lib.state.OperationFw;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitVal;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

public final class InvokeFuncCEnvFw {
    public static final Val invokeFuncCenv = telephonist("invokeFuncCenv", (arg, context) -> {
        if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"), context);
            Val compEnv = arg.call(symbol("comp-env"), context);
            Expr expr = exprVal._unpack();
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                Vit func = CompEnv.of(compEnv).compile(ExprFw.wrap(f), context);
                if (func == null)
                    return Val.unspecified;

                func = Vit.simplify(func, context);

                Val exprCallOp = ExprCallOpFw.exprCallOp.asVal().call(symbol("of-expr-list"), context).call(exprVal, context).call(compEnv, context);
                Vit vit = func.call(exprCallOp);

                if (func instanceof VitVal) {
                    Val codeToWhichFunctionJustCompiled = vit.eval(context);
                    if (!VitFw.isVit(codeToWhichFunctionJustCompiled.type())) {
                        return codeToWhichFunctionJustCompiled;
                    }
                    return codeToWhichFunctionJustCompiled;
                }

//                Vit resultingCode = Vit.val(VitFw.eval).call(vit).call(Vit.var);
                Vit resultingCode = Vit.invoke(Vit.val(OperationFw.wrap(Operation.vit(Vit.val(VitFw.eval).call(vit).call(Vit.var)))));

                return VitFw.wrap(resultingCode);
            }
        }
        return Val.unspecified;
    });
}
