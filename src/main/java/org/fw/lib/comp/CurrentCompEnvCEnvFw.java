package org.fw.lib.comp;

import org.fw.base.Val;
import org.fw.lib.VitFw;
import org.fw.lib.expr.SyntaxResolveFw;
import org.fw.vit.Vit;

import static org.fw.FW.symbol;
import static org.fw.FW.telephonist;

public final class CurrentCompEnvCEnvFw {
    public static final Val currentCompEnvCenv = telephonist("currentCompEnvCenv", (arg, context) -> {
        if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"), context);
            Val compEnv = arg.call(symbol("comp-env"), context);
            if (exprVal.equals(symbol("comp-env"))) {
                return VitFw.wrap(Vit.val(compEnv));
            }
        }
        return Val.unspecified;
    });
}
