package org.fw.lib.stdlib.expr;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Val;
import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import org.fw.lib.stdlib.VitFw;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;

import static org.fw.core.FW.symbol;

public final class ExprGetFw {

    public static Val handleDot(Symbol sym, CompEnv compEnv) {
        String fullQualifier = sym.getValue();
        int dotIndex = fullQualifier.lastIndexOf('.');
        if (dotIndex == -1)
            return null;
        String origin = fullQualifier.substring(0, dotIndex);
        String property = fullQualifier.substring(dotIndex + 1);

        Vit first;
        try {
            first = compEnv.compile(FW.symbol(origin));
        } catch (VitCompilationException e) {
            e.printStackTrace();
            return null;
        }

        return VitFw.wrap(first.call(symbol(property)));
    }

    public static final Val getterCEnv = FW.telephonist_native("dot-getters-cenv-fw", (arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = (Val) arg.call(FW.symbol("expr"));
            Val compEnv = (Val) arg.call(FW.symbol("comp-env"));
            Expr expr = exprVal._UNPACK_(Expr.class);
            if (expr instanceof Symbol) {
                // handling value.x
                return handleDot((Symbol) expr, CompEnv.of(compEnv));
            } else if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {

                // handling (get value x)
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) if (((Symbol) f).getValue().equals("get")) {
                    if (isize == 1) {
                        return null;
                    }

                    Val retVitV = (Val) compEnv.call(CompEnv.syntaxResolve(((Val) exprVal.call(DIntFw.dint(1)))._UNPACK_(Expr.class), CompEnv.of(compEnv)));
                    if (!VitFw.isVit(retVitV.getType()))
                        return retVitV; // compile error idk
                    Vit retVit = retVitV._UNPACK_();

                    for (int i = 1; i < (isize - 1); i++) {
                        Val property = (Val) exprVal.call(DIntFw.dint(i + 1));
                        if (!property.getType().equals(SymbolFw.symbol))
                            return null; // not a compile error idk (actually it still is)

                        retVit = retVit.call(Vit.val(property));
                    }

                    return VitFw.wrap(retVit);
                }
            }
        }
        return null;
    });

    public static final Lib lib = Lib.ofCEnv(getterCEnv);
}
