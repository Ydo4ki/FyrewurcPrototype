package org.fw.esast.expr.comp;

import org.fw.core.FW;
import org.fw.base.Val;
import org.fw.std.VitFw;
import org.fw.esast.expr.SyntaxResolveFw;
import org.fw.core.vit.Vit;

import static org.fw.core.FW.telephonist_native;

public final class CurrentCompEnvCEnvFw {
    public static final Val currentCompEnvCenv = FW.telephonist_native("currentCompEnvCenv", (arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = (Val) arg.call(FW.symbol("expr"));
            Val compEnv = (Val) arg.call(FW.symbol("comp-env"));
            if (exprVal.equalsSymbol("comp-env")) {
                return VitFw.wrap(Vit.val(compEnv));
            }
        }
        return null;
    });
}
