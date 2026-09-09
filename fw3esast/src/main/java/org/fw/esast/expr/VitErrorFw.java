package org.fw.esast.expr;

import com.ydo4ki.fw.internal.lib.stdlib.StrFw;
import com.ydo4ki.fw.internal.lib.ConstraintFw;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import com.ydo4ki.esast.Expr;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.std.*;

import static org.fw.core.FW.symbol;

public final class VitErrorFw {
    public static final Type vitError = StructFw.struct(
            DeclarationFw.declaration(symbol("expr"), ExprFw.isExpr),
            DeclarationFw.declaration(symbol("message"), (Val) ConstraintFw.toConstraint(StrFw.str))
    );

    public static Value rrror(Expr expr, String message) {
        return vitError.get("builder").call(ExprFw.wrap(expr)).call(StrFw.str(message));
    }

    public static final Val cantResolveAnythingCenv = FW.lambda_native("cantResolveAnythingCenv", (arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Value exprVal = (Val) arg.get("expr");
            Value compEnv = (Val) arg.get("comp-env");
            return vitError.get("builder").call(exprVal).call(StrFw.str("Can't resolve"));
        }
        return null;
    });

    public static final Lib lib = Lib.ofModule(ModuleFw.module(
            DeclaredFw.declared(symbol("VitError"), vitError)
    ));
}
