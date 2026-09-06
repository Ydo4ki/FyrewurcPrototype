package org.fw.lib.stdlib.expr;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.base.Val;
import org.fw.core.vit.Vit;
import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import org.fw.lib.stdlib.VitFw;

import static org.fw.core.FW.symbol;

public final class ToExprFw {

    public static final CompEnv directivesCenv = CompEnv.of(FW.telephonist_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.toExprResolve)) {
            Val val = arg.get("passing");
            Val compEnv = arg.get("chain");
            if (val.getType() == DIntFw.dint) {
                return ExprFw.wrap(Symbol.of(val._UNPACK().toString()));
            }
        }
        if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"));
            Val compEnv = arg.call(symbol("comp-env"));
            Expr expr = exprVal._UNPACK(Expr.class);
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) if (((Symbol) f).getValue().equals("to-expr")) {
                    if (isize != 2)
                        return null;

                    Val condition = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1))._UNPACK(Expr.class), CompEnv.of(compEnv)));
                    if (!VitFw.isVit(condition.getType()))
                        return null;
                    Vit v = condition._UNPACK();
                    return VitFw.wrap(Vit.val(compEnv).call(CompEnv.toExprResolve(v, CompEnv.of(compEnv))));
                } else if (((Symbol) f).getValue().equals("expr")) {
                    if (isize != 2)
                        return null;

                    return VitFw.wrap(Vit.val(exprVal.call(DIntFw.dint(1))));
                }
            }
        }
        return null;
    }));

    public static final Lib lib = Lib.ofCEnv(directivesCenv.asVal());
}
