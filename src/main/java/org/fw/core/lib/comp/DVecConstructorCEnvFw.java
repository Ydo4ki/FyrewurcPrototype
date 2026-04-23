package org.fw.core.lib.comp;

import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.base.Val;
import org.fw.core.lib.DVecFw;
import org.fw.core.lib.VitFw;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.ExprFw;
import org.fw.core.lib.expr.SyntaxResolveFw;
import org.fw.core.vit.Vit;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

public final class DVecConstructorCEnvFw {
    public static final Val dVecConstructorCenv = telephonist(() -> "dVecConstructorCenv", (arg, context) -> {
        if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"), context);
            Val compEnv = arg.call(symbol("comp-env"), context);
            Expr expr = exprVal._unpack();
            if (expr instanceof ExprList list && list.getBracketsType().equals(BracketsTypes.square)) {
                if (list.size() == 0)
                    return VitFw.wrap(Vit.val(DVecFw.dvecbf.call(DVecFw.emptyBuilder, context)));

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
