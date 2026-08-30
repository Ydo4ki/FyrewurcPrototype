package org.fw.lib.stdlib.expr;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Val;
import org.fw.lib.stdlib.DIntFw;
import org.fw.lib.stdlib.VitFw;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

public final class ExprGetFw {
    public static final Val getterCEnv = FW.telephonist("dot-getters-cenv-fw", (arg) -> {
        if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"));
            Val compEnv = arg.call(symbol("comp-env"));
            Expr expr = exprVal._unpack();
            if (expr instanceof Symbol) {
                // handling value.x

                Symbol sym = (Symbol) expr;
                String fullQualifier = sym.getValue();
                int dotIndex = fullQualifier.lastIndexOf('.');
                if (dotIndex == -1)
                    return null;
                String origin = fullQualifier.substring(0, dotIndex);
                String property = fullQualifier.substring(dotIndex + 1);

                Vit first = null;
                try {
                    first = CompEnv.of(compEnv).compile(FW.symbol(origin));
                } catch (VitCompilationException e) {
                    return null;
                }

                return VitFw.wrap(first.call(symbol(property)));
            } else if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                // handling (get value x)

                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) if (((Symbol) f).getValue().equals("get")) {
                    if (isize == 1) {
                        return null;
                    }

                    Val retVitV = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1))._unpack(), CompEnv.of(compEnv)));
                    if (!VitFw.isVit(retVitV.type()))
                        return retVitV; // compile error idk
                    Vit retVit = retVitV._unpack();

                    for (int i = 1; i < (isize - 1); i++) {
                        Val property = exprVal.call(DIntFw.dint(i + 1));
                        if (!property.type().equals(SymbolFw.symbol))
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
