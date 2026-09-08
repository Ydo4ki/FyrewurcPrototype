package org.fw.esast.expr;

import org.fw.core.FW;
import org.fw.esast.extern.BracketsTypes;
import org.fw.esast.extern.Expr;
import org.fw.esast.extern.ExprList;
import org.fw.esast.extern.Symbol;
import org.fw.base.Val;
import org.fw.core.vit.Vit;
import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import org.fw.std.VitFw;

public final class ToExprFw {

    public static final CompEnv directivesCenv = CompEnv.of(FW.telephonist_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.toExprResolve)) {
            Val val = (Val) arg.get("passing");
            Val compEnv = (Val) arg.get("chain");
            if (val.getType() == DIntFw.dint) {
                return ExprFw.wrap(Symbol.of(val._UNPACK_().toString()));
            }
        }
        if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = (Val) arg.call(FW.symbol("expr"));
            Val compEnv = (Val) arg.call(FW.symbol("comp-env"));
            Expr expr = exprVal._UNPACK_(Expr.class);
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) if (((Symbol) f).getValue().equals("to-expr")) {
                    if (isize != 2)
                        return null;

                    Val condition = (Val) compEnv.call(CompEnv.syntaxResolve(((Val) exprVal.call(DIntFw.dint(1)))._UNPACK_(Expr.class), CompEnv.of(compEnv)));
                    if (!VitFw.isVit(condition.getType()))
                        return null;
                    Vit v = condition._UNPACK_();
                    return VitFw.wrap(Vit.val(compEnv).call(CompEnv.toExprResolve(v, CompEnv.of(compEnv))));
                } else if (((Symbol) f).getValue().equals("expr")) {
                    if (isize != 2)
                        return null;

                    return VitFw.wrap(Vit.val((Val) exprVal.call(DIntFw.dint(1))));
                }
            }
        }
        return null;
    }));

    public static final Lib lib = Lib.ofCEnv(directivesCenv.asValue());
}
