package org.fw.lib.stdlib.expr;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.base.Val;
import org.fw.core.vit.VitUtils;
import org.fw.lib.stdlib.DIntFw;
import org.fw.lib.stdlib.FunctionFw;
import org.fw.lib.stdlib.VitFw;
import org.fw.lib.stdlib.dvec.DVecBuilderFw;
import org.fw.core.vit.Vit;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

// todo: make this generate a code that constructs vit so we can make old cenv static instead of storing it with the function
public final class FnCallFw {
    public static final Val fnCallCEnv = FW.telephonist((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.toFnResolve)) {
            Val val = arg.get("passing");
            Val compEnv = arg.get("chain");
            if (val.getType() == FunctionFw.function) {
                return val.get("fn-call");
            }
        }
        if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"));
            Val compEnv = arg.call(symbol("comp-env"));
            Expr expr = exprVal._unpack(Expr.class);
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();

                Val fvv = compEnv.call(CompEnv.syntaxResolve(f, CompEnv.of(compEnv)));
                if (!VitFw.isVit(fvv.getType()))
                    return null;
                Vit fv = VitFw.unwrap(fvv, f);

                Vit varValuesV = Vit.val(DVecBuilderFw.emptyBuilder);
                for (int i = 1; i < isize; i++) {
                    Expr eee = exprVal.call(DIntFw.dint(i))._unpack(Expr.class);
                    varValuesV = varValuesV.call(VitFw.unwrap(compEnv.call(CompEnv.syntaxResolve(eee, CompEnv.of(compEnv))), eee));
                }
                varValuesV = Vit.val(DVecBuilderFw.dvecbf).call(varValuesV);

//                Vit getop = fv.call(symbol("fn-call")).call(VitUtils.simplify(varValuesV));
                Vit getop = Vit.val(compEnv).call(CompEnv.toFnResolve(fv, CompEnv.of(compEnv)))
                        .call(VitUtils.simplify(varValuesV));
                return VitFw.wrap(Vit.invoke(getop));
            }
        }
        return null;
    });

    public static final Lib lib = Lib.ofCEnv(fnCallCEnv);
}
