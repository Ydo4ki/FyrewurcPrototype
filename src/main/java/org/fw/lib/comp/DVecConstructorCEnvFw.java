package org.fw.lib.comp;

import org.fw.ast.BracketsTypes;
import org.fw.ast.Expr;
import org.fw.ast.ExprList;
import org.fw.base.Val;
import org.fw.lib.DVecFw;
import org.fw.lib.VitFw;
import org.fw.lib.expr.CompEnv;
import org.fw.lib.expr.ExprFw;
import org.fw.lib.expr.SyntaxResolveFw;
import org.fw.vit.Vit;

import static org.fw.FW.symbol;
import static org.fw.FW.telephonist;

public final class DVecConstructorCEnvFw {
    public static final Val dVecConstructorCenv = telephonist(() -> "dVecConstructorCenv", (arg, context) -> {
        if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"), context);
            Val compEnv = arg.call(symbol("comp-env"), context);
            Expr expr = exprVal._unpack();
            if (expr instanceof ExprList list && list.getBracketsType().equals(BracketsTypes.square)) {
                if (list.size() == 0)
                    return DVecFw.dvecbf.call(DVecFw.emptyBuilder, context);

                Vit ctor = Vit.val(DVecFw.emptyBuilder);
                for (int i = 0; i < list.size(); i++) {
                    Expr f = list.get(i);
                    Val elVitVal = CompEnv.of(compEnv).compileV(ExprFw.wrap(f), context);
                    if (!VitFw.isVit(elVitVal.type()))
                        return elVitVal;

                    Vit vit = VitFw.unwrap(elVitVal);
                    assert vit != null;
                    ctor = ctor.call(Vit.simplify(vit, context));
                }

                ctor = Vit.val(DVecFw.dvecbf).call(ctor);

                return VitFw.wrap(ctor);
            }
        }
        return Val.unspecified;
    });
}
