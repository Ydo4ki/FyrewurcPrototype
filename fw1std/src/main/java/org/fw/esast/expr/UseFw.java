package org.fw.esast.expr;

import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import com.ydo4ki.esast.BracketsTypes;
import com.ydo4ki.esast.Expr;
import com.ydo4ki.esast.ExprList;
import com.ydo4ki.esast.Symbol;
import org.fw.base.Val;
import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import org.fw.std.ModuleFw;
import org.fw.std.VitFw;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitUtils;
import org.fw.core.vit.VitVal;

public final class UseFw {
    public static final Val useDirectivesCenv = FW.lambda_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = (Val) arg.call(FW.symbol("expr"));
            Val compEnv = (Val) arg.call(FW.symbol("comp-env"));
            Expr expr = ExprFw.unwrap(exprVal);
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case "usem": {
                        if (isize != 3)
                            return null;

                        Val moduleVit = (Val) compEnv.call(CompEnv.syntaxResolve(((Val) exprVal.call(DIntFw.dint(1)))._UNPACK_(), CompEnv.of(compEnv)));
                        if (!VitFw.isVit(moduleVit.getType()))
                            return null; // could not compile module

                        Vit vit = VitUtils.simplify((Vit) moduleVit._UNPACK_());
                        if (!(vit instanceof VitVal))
                            return null; // this is meant to be known at compile-time

                        Value newCompEnv = CompEnv.compEnv(
                                compEnv,
                                ModuleFw.ModuleCEnvFw.compEnv((Val)((VitVal) vit).val())
                        );

                        Val value = (Val) newCompEnv.call(CompEnv.syntaxResolve(((Val) exprVal.call(DIntFw.dint(2)))._UNPACK_(), CompEnv.of(newCompEnv)));
                        if (!VitFw.isVit(value.getType()))
                            return value; // error idk

                        return value;
                    }
                    case "usec": {
                        if (isize != 3)
                            return null;

                        Val cEnvVit = (Val) compEnv.call(CompEnv.syntaxResolve(((Val) exprVal.call(DIntFw.dint(1)))._UNPACK_(), CompEnv.of(compEnv)));
                        if (!VitFw.isVit(cEnvVit.getType()))
                            return null; // could not compile cenv

                        Vit vit = VitUtils.simplify((Vit) cEnvVit._UNPACK_());
                        if (!(vit instanceof VitVal))
                            return null; // this is meant to be known at compile-time

                        Value newCompEnv = CompEnv.compEnv(
                                compEnv,
                                ((VitVal) vit).val()
                        );

                        Val value = (Val) newCompEnv.call(CompEnv.syntaxResolve(((Val) exprVal.call(DIntFw.dint(2)))._UNPACK_(), CompEnv.of(newCompEnv)));
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
