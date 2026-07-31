package org.fw.core.lib.comp.legacy;

import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.base.Val;
import org.fw.core.base.context.Context;
import org.fw.core.base.context.RtEnv;
import org.fw.core.lib.VitFw;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.ExprCallOpFw;
import org.fw.core.lib.expr.ExprFw;
import org.fw.core.lib.expr.SyntaxResolveFw;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;
import org.fw.core.state.operation.OperationFw;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;
import org.fw.core.vit.VitVal;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

// sorry I had to bring this back because as legacy code of horiforge
@Deprecated
public final class InvokeFuncCEnvFw {

    @Deprecated // this just converts it to operation, it does not evaluate anything (which leaves no room for local evaluations)
    public static final Val eval = telephonist("eval", (arg) -> {
        if (VitFw.isVit(arg.type())) {
            Vit vit = arg._unpack();
            return telephonist((env) -> {
                return State.performAndDie(scope ->
                        OperationFw.wrap(Operation.vit(Vit.reduce(vit, RtEnv.of(env)), RtEnv.of(env))));
//                return ;
            });
        }
        return null;
    });
    @Deprecated
    public static final Val invokeFuncCenv = telephonist("invokeFuncCenv", (arg) -> {
        if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"));
            Val compEnv = arg.call(symbol("comp-env"));
            Expr expr = exprVal._unpack();
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                Vit func = null;
                try {
                    func = CompEnv.of(compEnv).compile(ExprFw.wrap(f));
                } catch (VitCompilationException e) {
                    return null;
                }

                func = Vit.simplify(func);

                Val exprCallOp = ExprCallOpFw.exprCallOp.asVal().call(symbol("of-expr-list")).call(exprVal).call(compEnv);
                Vit vit = func.call(exprCallOp);

                if (func instanceof VitVal) {
                    Val codeToWhichFunctionJustCompiled = vit.eval();
                    if (!VitFw.isVit(codeToWhichFunctionJustCompiled.type())) {
                        return codeToWhichFunctionJustCompiled;
                    }
                    return codeToWhichFunctionJustCompiled;
                }

//                Vit resultingCode = Vit.val(VitFw.eval).call(vit).call(Vit.var);
                Vit resultingCode = Vit.invoke(Vit.val(OperationFw.wrap(Operation.vit(Vit.val(eval).call(vit).call(Vit.var), Context.outOf.rtEnv())))); // ._.

                return VitFw.wrap(resultingCode);
            }
        }
        return null;
    });
}
