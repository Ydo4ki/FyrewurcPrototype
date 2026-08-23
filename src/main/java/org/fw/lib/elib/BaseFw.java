package org.fw.lib.elib;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.base.*;
import org.fw.lib.elib.expr.CompEnv;
import org.fw.lib.elib.expr.SyntaxResolveFw;
import org.fw.core.vit.Vit;
import org.fw.lib.elib.expr.ToExprFn;

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
                        return null;

                    return VitFw.wrap(Vit.call(ValsFw.typeGet, operand._unpack(Vit.class)));
                }
            }
        }
        return null;
    });


    public static final Val boolToExpr = FW.telephonist((arg) -> {
        if (arg.type() != ToExprFn.toExprResolve)
            return null;
        Val toExpr = arg.call(symbol("chain"));
        arg = arg.call(symbol("passing"));

        Type type = arg.type();
        if (type.equals(BoolFw.bool)) {
            return symbol(arg._unpack().toString());
        }
        return null;
    });

    public static final Lib boolLib = Lib.of(
            ModuleFw.module(
                    DeclaredFw.declared(symbol("true"), BoolFw._true),
                    DeclaredFw.declared(symbol("false"), BoolFw._false),
                    DeclaredFw.declared(symbol("Bool"), BoolFw.bool)
            ),
            var -> ChainLinkFw.chain(ToExprFn.exprififier,
                    boolToExpr,
                    var.call(symbol("to-expr"))
            )
    );

    public static final Lib lib = Lib.combine(Lib.of(
            ModuleFw.module(
                    DeclaredFw.declared(symbol("Call"), Call.call_t.asVal()),
                    DeclaredFw.declared(symbol("Telephonist"), Val.ofTelephonist(0)),
                    DeclaredFw.declared(symbol("Symbol"), SymbolFw.symbol.asVal()),
                    DeclaredFw.declared(symbol("is-unspecified"), ValsFw.isUnspecified),
                    DeclaredFw.declared(symbol("eq"), ValsFw.eq),
                    DeclaredFw.declared(symbol("type-get"), ValsFw.typeGet)
            ),
            directivesCenv
    ), boolLib);
}
