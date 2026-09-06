package org.fw.lib.stdlib.expr;

import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.base.Val;
import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import org.fw.lib.stdlib.ModuleFw;
import org.fw.lib.stdlib.VitFw;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitUtils;
import org.fw.core.vit.VitVal;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist_native;

public final class UseFw {
    public static final Val useDirectivesCenv = FW.telephonist_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"));
            Val compEnv = arg.call(symbol("comp-env"));
            Expr expr = exprVal._UNPACK(Expr.class);
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case "usem": {
                        if (isize != 3)
                            return null;

                        Val moduleVit = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1))._UNPACK(), CompEnv.of(compEnv)));
                        if (!VitFw.isVit(moduleVit.getType()))
                            return null; // could not compile module

                        Vit vit = VitUtils.simplify(moduleVit._UNPACK(Vit.class));
                        if (!(vit instanceof VitVal))
                            return null; // this is meant to be known at compile-time

                        Value newCompEnv = CompEnv.compEnv(
                                compEnv,
                                ModuleFw.ModuleCEnvFw.compEnv((Val)((VitVal) vit).val())
                        );

                        Val value = (Val) newCompEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(2))._UNPACK(), CompEnv.of(newCompEnv)));
                        if (!VitFw.isVit(value.getType()))
                            return value; // error idk

                        return value;
                    }
                    case "usec": {
                        if (isize != 3)
                            return null;

                        Val cEnvVit = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1))._UNPACK(), CompEnv.of(compEnv)));
                        if (!VitFw.isVit(cEnvVit.getType()))
                            return null; // could not compile cenv

                        Vit vit = VitUtils.simplify(cEnvVit._UNPACK(Vit.class));
                        if (!(vit instanceof VitVal))
                            return null; // this is meant to be known at compile-time

                        Value newCompEnv = CompEnv.compEnv(
                                compEnv,
                                ((VitVal) vit).val()
                        );

                        Val value = (Val) newCompEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(2))._UNPACK(), CompEnv.of(newCompEnv)));
                        if (!VitFw.isVit(value.getType()))
                            return value; // error idk

                        return value;
                    }
                    // todo: 'use' for libs
                    //  or it could also be import
                }
            }
        }
        return null;
    });

    public static final Lib lib = Lib.ofCEnv(useDirectivesCenv);
}
