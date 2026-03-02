package org.fw.lib.comp;

import org.fw.ast.BracketsTypes;
import org.fw.ast.Expr;
import org.fw.ast.ExprList;
import org.fw.base.Val;
import org.fw.lib.VitFw;
import org.fw.lib.expr.CompEnv;
import org.fw.lib.expr.ExprCallOpFw;
import org.fw.lib.expr.ExprFw;
import org.fw.lib.expr.SyntaxResolveFw;
import org.fw.state.operation.Operation;
import org.fw.lib.state.OperationFw;
import org.fw.vit.Vit;
import org.fw.vit.VitVal;

import static org.fw.FW.symbol;
import static org.fw.FW.telephonist;

public final class InvokeFuncCEnvFw {
    public static final Val invokeFuncCenv = telephonist("invokeFuncCenv", (arg, context) -> {
        if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"), context);
            Val compEnv = arg.call(symbol("comp-env"), context);
            Expr expr = exprVal._unpack();
            if (expr instanceof ExprList list && list.getBracketsType().equals(BracketsTypes.round) && list.size() > 0) {
                Expr f = list.get(0);
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
