package org.fw.esast.expr.forstd;

import com.ydo4ki.esast.BracketsTypes;
import com.ydo4ki.esast.Expr;
import com.ydo4ki.esast.ExprList;
import com.ydo4ki.esast.Symbol;
import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import org.fw.base.TypeGetFw;
import org.fw.base.Unspecified;
import org.fw.base.Val;
import org.fw.core.FW;
import org.fw.core.vit.Vit;
import org.fw.esast.expr.CompEnv;
import org.fw.esast.expr.ExprFw;
import org.fw.esast.expr.Lib;
import org.fw.esast.expr.SyntaxResolveFw;
import org.fw.std.BaseFw;
import org.fw.std.VitFw;

import static org.fw.core.FW.symbol;

public final class BaseLib {
    private static final Val directivesCenv = FW.lambda_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = (Val) arg.call(FW.symbol("expr"));
            Val compEnv = (Val) arg.call(FW.symbol("comp-env"));
            Expr expr = ExprFw.unwrap(exprVal);
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) if (((Symbol) f).getValue().equals("typeof")) {
                    if (isize != 2)
                        return null;

                    Val val = ((Val) (Val) exprVal.call(DIntFw.dint(1)));
                    Val operand = (Val) (Val) compEnv.call(CompEnv.syntaxResolve((Expr) ExprFw.unwrap(val), CompEnv.of(compEnv)));
                    if (!VitFw.isVit(operand.getType()))
                        return operand;

                    return VitFw.wrap(Vit.call(TypeGetFw.typeGet, (Vit) operand._UNPACK_()));
                } else if (((Symbol) f).getValue().equals("specified")) {
                    if (isize != 2)
                        return null;

                    Val val = ((Val) (Val) exprVal.call(DIntFw.dint(1)));
                    Val operand = (Val) (Val) compEnv.call(CompEnv.syntaxResolve((Expr) ExprFw.unwrap(val), CompEnv.of(compEnv)));
                    if (!VitFw.isVit(operand.getType()))
                        return operand;

                    return VitFw.wrap(Vit.call(Unspecified.isUnspecified, (Vit) operand._UNPACK_()).call(symbol("not")));
                }
            }
        }
        return null;
    });

    public static final Lib lib = Lib.combine(Lib.of(
            BaseFw.module,
            directivesCenv
    ));
}
