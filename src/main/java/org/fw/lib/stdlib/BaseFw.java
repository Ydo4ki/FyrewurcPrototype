package org.fw.lib.stdlib;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.base.*;
import org.fw.lib.stdlib.expr.CompEnv;
import org.fw.lib.stdlib.expr.Lib;
import org.fw.lib.stdlib.expr.SyntaxResolveFw;
import org.fw.core.vit.Vit;

import static org.fw.core.FW.symbol;

public final class BaseFw {

    private static final Val directivesCenv = FW.telephonist((arg) -> {
        if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"));
            Val compEnv = arg.call(symbol("comp-env"));
            Expr expr = exprVal._unpack();
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) if (((Symbol) f).getValue().equals("typeof")) {
                    if (isize != 2)
                        return null;

                    Val operand = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1))._unpack(), CompEnv.of(compEnv)));
                    if (!VitFw.isVit(operand.type()))
                        return operand;

                    return VitFw.wrap(Vit.call(TypeGetFw.typeGet, operand._unpack(Vit.class)));
                } else if (((Symbol) f).getValue().equals("specified")) {
                    if (isize != 2)
                        return null;

                    Val operand = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1))._unpack(), CompEnv.of(compEnv)));
                    if (!VitFw.isVit(operand.type()))
                        return operand;

                    return VitFw.wrap(Vit.call(Unspecified.isUnspecified, operand._unpack(Vit.class)).call(symbol("not")));
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
