package org.fw.core.lib.expr;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.base.Val;
import org.fw.core.lib.DIntFw;
import org.fw.core.lib.VitFw;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;

import java.math.BigInteger;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

public final class ExprGetFw {
    public static final Val getterCEnv = telephonist("dot-getters-cenv-fw", (arg, context) -> {
        if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"), context);
            Val compEnv = arg.call(symbol("comp-env"), context);
            Expr expr = exprVal._unpack();
            if (expr instanceof Symbol) {
                // handling value.x

                Symbol sym = (Symbol) expr;
                String fullQualifier = sym.getValue();
                int dotIndex = fullQualifier.lastIndexOf('.');
                if (dotIndex == -1)
                    return Val.unspecified;
                String origin = fullQualifier.substring(0, dotIndex);
                String property = fullQualifier.substring(dotIndex + 1);

                Vit first = null;
                try {
                    first = CompEnv.of(compEnv).compile(FW.symbol(origin), context);
                } catch (VitCompilationException e) {
                    return Val.unspecified;
                }

                return VitFw.wrap(first.call(symbol(property)));
            } else if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                // handling (get value x)

                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) if (((Symbol) f).getValue().equals("get")) {
                    if (isize == 1) {
                        return Val.unspecified;
                    }

                    Val retVit = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(compEnv)), context);
                    if (!VitFw.isVit(retVit.type()))
                        return retVit; // compile error idk

                    for (int i = 1; i < (isize - 1); i++) {
                        Val property = exprVal.call(DIntFw.dint(i + 1), context);
                        if (!property.type().equals(ExprFw.symbol))
                            return Val.unspecified; // not a compile error idk (actually it still is)

                        retVit = VitFw.wrap(VitFw.unwrap0(retVit).call(Vit.val(property)));
                    }

                    return retVit;
                }
            }
        }
        return Val.unspecified;
    });
}
