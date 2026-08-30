package org.fw.lib.stdlib.expr;

import org.fw.core.FW;
import org.fw.core.ast.Expr;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.lib.stdlib.*;

import static org.fw.core.FW.symbol;

public final class VitErrorFw {
    public static final Type vitError = StructFw.struct(
            DeclarationFw.declaration(symbol("expr"), ExprFw.isExpr),
            DeclarationFw.declaration(symbol("message"), ConstraintFw.toConstraint(StrFw.str))
    );

    public static Val rrror(Expr expr, String message) {
        return vitError.get("builder").call(ExprFw.wrap(expr)).call(StrFw.str(message));
    }

    public static final Val cantResolveAnythingCenv = FW.telephonist((arg) -> {
        if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"));
            Val compEnv = arg.call(symbol("comp-env"));
            Expr expr = exprVal._unpack();
            Val error = rrror(expr, "Can't resolve");
            return error;
        }
        return null;
    });

    public static final Lib lib = Lib.ofModule(ModuleFw.module(
            DeclaredFw.declared(symbol("VitError"), vitError)
    ));
}
