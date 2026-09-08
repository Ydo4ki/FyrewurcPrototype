package org.fw.esast.expr;

import org.fw.core.FW;
import com.ydo4ki.esast.BracketsTypes;
import com.ydo4ki.esast.Expr;
import com.ydo4ki.esast.ExprList;
import com.ydo4ki.esast.Symbol;
import org.fw.base.Val;
import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import org.fw.std.VitFw;
import org.fw.core.vit.Vit;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist_native;

public final class OperatorsFw {

    public static final Val exports = FW.telephonist_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = (Val) (Val) arg.call(FW.symbol("expr"));
            Val compEnv = (Val) (Val) arg.call(FW.symbol("comp-env"));
            Expr expr = (Expr) ExprFw.unwrap(exprVal);
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) {
                    String name = ((Symbol) f).getValue();
                    switch (name) {
                        // accumulators
                        case "and":
                        case "or":
                        case "xor":

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
                                Val val = ((Val) (Val) exprVal.call(DIntFw.dint(i)));
                                Val term = (Val) (Val) compEnv.call(CompEnv.syntaxResolve((Expr) ExprFw.unwrap(val), CompEnv.of(compEnv)));
                                if (!VitFw.isVit(term.getType()))
                                    return null;
                                if (vit == null) vit = term._UNPACK_(Vit.class);
                                else vit = vit.call(symbol(name)).call(term._UNPACK_(Vit.class));
                            }
                            return VitFw.wrap(vit);
                        }
                        // senders
                        case "not":
                        case "neg":
                        case "~": {
                            if (isize != 2)
                                return null;

                            Val val = ((Val) (Val) exprVal.call(DIntFw.dint(1)));
                            Val term = (Val) (Val) compEnv.call(CompEnv.syntaxResolve((Expr) ExprFw.unwrap(val), CompEnv.of(compEnv)));
                            if (!VitFw.isVit(term.getType()))
                                return null;
                            return VitFw.wrap(term._UNPACK_(Vit.class).call(symbol(name)));
                        }
                    }
                }
            }
        }
        return null;
    });

    public static final Lib lib = Lib.ofCEnv(exports);
}
