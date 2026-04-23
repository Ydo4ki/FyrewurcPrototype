package org.fw.core.lib.comp;

import org.fw.core.ast.Expr;
import org.fw.core.ast.Symbol;
import org.fw.core.base.Val;
import org.fw.core.lib.VitFw;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.SyntaxResolveFw;
import org.fw.core.vit.Vit;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

public final class DotGettersCEnvFw {
    public static final Val cenv = telephonist("dot-getters-cenv-fw", (arg, context) -> {
        if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"), context);
            Val compEnv = arg.call(symbol("comp-env"), context);
            Expr expr = exprVal._unpack();
            if (expr instanceof Symbol sym) {
                String fullQualifier = sym.getValue();
                int dotIndex = fullQualifier.lastIndexOf('.');
                if (dotIndex == -1)
                    return Val.unspecified;
                String origin = fullQualifier.substring(0, dotIndex);
                String property = fullQualifier.substring(dotIndex + 1);

                Vit first = CompEnv.of(compEnv).compile(symbol(origin), context);
                if (first == null)
                    return Val.unspecified;

                return VitFw.wrap(first.call(symbol(property)));
            }
        }
        return Val.unspecified;
    });
}
