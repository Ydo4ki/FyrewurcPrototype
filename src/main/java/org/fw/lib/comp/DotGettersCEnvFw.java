package org.fw.lib.comp;

import org.fw.ast.Expr;
import org.fw.ast.Symbol;
import org.fw.base.Val;
import org.fw.lib.VitFw;
import org.fw.lib.expr.CompEnv;
import org.fw.lib.expr.SyntaxResolveFw;
import org.fw.vit.Vit;

import static org.fw.FW.symbol;
import static org.fw.FW.telephonist;

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
