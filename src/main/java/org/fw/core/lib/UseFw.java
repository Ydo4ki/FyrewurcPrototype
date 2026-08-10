package org.fw.core.lib;

import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.base.Val;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.SyntaxResolveFw;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitVal;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

public final class UseFw {
    public static final CompEnv useDirectivesCenv = CompEnv.of(telephonist((arg) -> {
        if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"));
            Val compEnv = arg.call(symbol("comp-env"));
            Expr expr = exprVal._unpack();
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case "usem": {
                        if (isize != 3)
                            return null;

                        Val moduleVit = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1))._unpack(), CompEnv.of(compEnv)));
                        if (!VitFw.isVit(moduleVit.type()))
                            return null; // could not compile module

                        Vit vit = Vit.simplify(moduleVit._unpack(Vit.class));
                        if (!(vit instanceof VitVal))
                            return null; // this is meant to be known at compile-time

                        Val newCompEnv = CompEnv.compEnv(
                                compEnv,
                                ModuleFw.ModuleCEnvFw.compEnv(((VitVal) vit).val())
                        );

                        Val value = newCompEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(2))._unpack(), CompEnv.of(newCompEnv)));
                        if (!VitFw.isVit(value.type()))
                            return value; // error idk

                        return value;
                    }
                    case "usec": {
                        if (isize != 3)
                            return null;

                        Val cEnvVit = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1))._unpack(), CompEnv.of(compEnv)));
                        if (!VitFw.isVit(cEnvVit.type()))
                            return null; // could not compile cenv

                        Vit vit = Vit.simplify(cEnvVit._unpack(Vit.class));
                        if (!(vit instanceof VitVal))
                            return null; // this is meant to be known at compile-time

                        Val newCompEnv = CompEnv.compEnv(
                                compEnv,
                                ((VitVal) vit).val()
                        );

                        Val value = newCompEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(2))._unpack(), CompEnv.of(newCompEnv)));
                        if (!VitFw.isVit(value.type()))
                            return value; // error idk

                        return value;
                    }
                    // todo: 'use' for libs
                    //  or it could also be import
                }
            }
        }
        return null;
    }));

    public static final Lib lib = Lib.of(useDirectivesCenv);
}
