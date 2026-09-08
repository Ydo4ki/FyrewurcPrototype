package org.fw.esast.expr;

import org.fw.core.FW;
import com.ydo4ki.esast.BracketsTypes;
import com.ydo4ki.esast.Expr;
import com.ydo4ki.esast.ExprList;
import org.fw.base.Val;
import org.fw.core.vit.VitUtils;
import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import org.fw.std.FunctionFw;
import org.fw.std.VitFw;
import org.fw.std.dvec.DVecBuilderFw;
import org.fw.core.vit.Vit;

import static org.fw.core.FW.telephonist_native;

// todo: make this generate a code that constructs vit so we can make old cenv static instead of storing it with the function
public final class FnCallFw {
    public static final Val fnCallCEnv = FW.telephonist_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.toFnResolve)) {
            Val val = (Val) arg.get("passing");
            Val compEnv = (Val) arg.get("chain");
            if (val.getType() == FunctionFw.function) {
                return (Val) val.get("fn-call");
            }
        }
        if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = (Val) arg.call(FW.symbol("expr"));
            Val compEnv = (Val) arg.call(FW.symbol("comp-env"));
            Expr expr = (Expr) ExprFw.unwrap(exprVal);
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();

                Val fvv = (Val) compEnv.call(CompEnv.syntaxResolve(f, CompEnv.of(compEnv)));
                if (!VitFw.isVit(fvv.getType()))
                    return null;
                Vit fv = VitFw.unwrap(fvv, f);

                Vit varValuesV = Vit.val(DVecBuilderFw.emptyBuilder);
                for (int i = 1; i < isize; i++) {
                    Val val = ((Val) exprVal.call(DIntFw.dint(i)));
                    Expr eee = (Expr) ExprFw.unwrap(val);
                    varValuesV = varValuesV.call(VitFw.unwrap((Val) compEnv.call(CompEnv.syntaxResolve(eee, CompEnv.of(compEnv))), eee));
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
