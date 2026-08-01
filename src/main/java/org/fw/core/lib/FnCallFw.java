package org.fw.core.lib;

import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.base.Val;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.SyntaxResolveFw;
import org.fw.core.vit.Vit;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

public final class FnCallFw {
    public static final CompEnv fnCallCenv = CompEnv.of(telephonist((arg) -> {
        if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"));
            Val compEnv = arg.call(symbol("comp-env"));
            Expr expr = exprVal._unpack();
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();

                Val fvv = compEnv.call(CompEnv.syntaxResolve(f, CompEnv.of(compEnv)));
                if (!VitFw.isVit(fvv.type()))
                    return null;
                Vit fv = VitFw.unwrap(fvv);

                Vit varValuesV = Vit.val(DVecFw.emptyBuilder);
                for (int i = 1; i < isize; i++) {
                    varValuesV = varValuesV.call(VitFw.unwrap(compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(i))._unpack(), CompEnv.of(compEnv)))));
                }
                varValuesV = Vit.val(DVecFw.dvecbf).call(varValuesV);

                return VitFw.wrap(Vit.invoke(fv.call(symbol("fn-call")).call(varValuesV)));
            }
        }
        return null;
    }));
}
