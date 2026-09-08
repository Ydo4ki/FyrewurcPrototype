package org.fw.std;

import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import org.fw.base.*;
import org.fw.core.FW;
import com.ydo4ki.esast.BracketsTypes;
import com.ydo4ki.esast.Expr;
import com.ydo4ki.esast.ExprList;
import com.ydo4ki.esast.Symbol;

import org.fw.esast.expr.CompEnv;
import org.fw.esast.expr.ExprFw;
import org.fw.esast.expr.Lib;
import org.fw.esast.expr.SyntaxResolveFw;
import org.fw.core.vit.Vit;

import static org.fw.core.FW.symbol;

public final class BaseFw {

    private static final Val directivesCenv = FW.lambda_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = (Val) (Val) arg.call(FW.symbol("expr"));
            Val compEnv = (Val) (Val) arg.call(FW.symbol("comp-env"));
            Expr expr = (Expr) ExprFw.unwrap(exprVal);
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

    public static final Val module = ModuleFw.module(
            DeclaredFw.declared(symbol("Call"), CallFw.call_t.asVal()),
            DeclaredFw.declared(symbol("Telephonist"), Val.ofTelephonist(0)),
            DeclaredFw.declared(symbol("Symbol"), SymbolFw.symbol.asVal()),
            DeclaredFw.declared(symbol("is-unspecified"), Unspecified.isUnspecified),
            DeclaredFw.declared(symbol("eq"), EqFw.eq),
            DeclaredFw.declared(symbol("type-get"), TypeGetFw.typeGet),
            DeclaredFw.declared(symbol("Bool"), BoolFw.bool),
            DeclaredFw.declared(symbol("true"), BoolFw._true),
            DeclaredFw.declared(symbol("false"), BoolFw._false)
    );

    public static final Lib lib = Lib.combine(Lib.of(
            module,
            directivesCenv
    ));
}
