package org.fw.lib.elib;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.base.Val;
import org.fw.lib.elib.expr.CompEnv;
import org.fw.lib.elib.expr.SyntaxResolveFw;
import org.fw.core.vit.Vit;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

public final class OperatorsFw {

    public static final Val exports = FW.telephonist((arg) -> {
        if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"));
            Val compEnv = arg.call(symbol("comp-env"));
            Expr expr = exprVal._unpack();
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) {
                    String name = ((Symbol) f).getValue();
                    switch (name) {
                        // accumulators
                        case "+":
                        case "-":
                        case "*":
                        case "/":
                        case "%":
                        case "^^":

                        case "|":
                        case "&":
                        case "^":
                        case "~|":
                        case "~&":
                        case "~^":
                        case ">>":
                        case ">>>":
                        case ">>>>":
                        case "<<":
                        case "<<<":
                        case "<<<<":

                        case "<=>": {
                            Vit vit = null;
                            if (isize < 2)
                                return null;

                            for (int i = 1; i < isize; i++) {
                                Val term = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(i))._unpack(), CompEnv.of(compEnv)));
                                if (!VitFw.isVit(term.type()))
                                    return null;
                                if (vit == null) vit = term._unpack(Vit.class);
                                else vit = vit.call(symbol(name)).call(term._unpack(Vit.class));
                            }
                            return VitFw.wrap(vit);
                        }
                        // senders
                        case "not":
                        case "~": {
                            if (isize != 2)
                                return null;

                            Val term = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1))._unpack(), CompEnv.of(compEnv)));
                            if (!VitFw.isVit(term.type()))
                                return null;
                            return VitFw.wrap(term._unpack(Vit.class).call(symbol(name)));
                        }
                    }
                }
            }
        }
        return null;
    });

    public static final Lib lib = Lib.ofCEnv(exports);
}
