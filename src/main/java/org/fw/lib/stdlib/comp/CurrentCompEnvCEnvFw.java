package org.fw.lib.stdlib.comp;

import org.fw.core.FW;
import org.fw.core.base.Val;
import org.fw.lib.stdlib.VitFw;
import org.fw.lib.stdlib.expr.SyntaxResolveFw;
import org.fw.core.vit.Vit;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

public final class CurrentCompEnvCEnvFw {
    public static final Val currentCompEnvCenv = FW.telephonist("currentCompEnvCenv", (arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"));
            Val compEnv = arg.call(symbol("comp-env"));
            if (exprVal.equalsSymbol("comp-env")) {
                return VitFw.wrap(Vit.val(compEnv));
            }
        }
        return null;
    });
}
