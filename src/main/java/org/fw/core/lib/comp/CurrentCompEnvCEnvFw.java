package org.fw.core.lib.comp;

import org.fw.core.base.Unspecified;
import org.fw.core.base.Val;
import org.fw.core.lib.VitFw;
import org.fw.core.lib.expr.SyntaxResolveFw;
import org.fw.core.vit.Vit;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

public final class CurrentCompEnvCEnvFw {
    public static final Val currentCompEnvCenv = telephonist("currentCompEnvCenv", (arg, context) -> {
        if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"), context);
            Val compEnv = arg.call(symbol("comp-env"), context);
            if (exprVal.equals(symbol("comp-env"))) {
                return VitFw.wrap(Vit.val(compEnv));
            }
        }
        return Unspecified.unspecified;
    });
}
