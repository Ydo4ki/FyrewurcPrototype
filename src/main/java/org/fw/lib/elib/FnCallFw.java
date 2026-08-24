package org.fw.lib.elib;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.base.Val;
import org.fw.core.vit.VitUtils;
import org.fw.lib.elib.dvec.DVecBuilderFw;
import org.fw.lib.elib.expr.CompEnv;
import org.fw.lib.elib.expr.SyntaxResolveFw;
import org.fw.core.vit.Vit;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

public final class FnCallFw {
    public static final Val fnCallCenv = FW.telephonist((arg) -> {
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

                Vit varValuesV = Vit.val(DVecBuilderFw.emptyBuilder);
                for (int i = 1; i < isize; i++) {
                    varValuesV = varValuesV.call(VitFw.unwrap(compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(i))._unpack(), CompEnv.of(compEnv)))));
                }
                varValuesV = Vit.val(DVecBuilderFw.dvecbf).call(varValuesV);

                return VitFw.wrap(Vit.invoke(fv.call(symbol("fn-call")).call(VitUtils.simplify(varValuesV))));
            }
        }
        return null;
    });

    public static final Lib lib = Lib.ofCEnv(fnCallCenv);
}
