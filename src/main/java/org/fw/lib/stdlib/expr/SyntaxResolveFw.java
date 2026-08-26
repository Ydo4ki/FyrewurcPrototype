package org.fw.lib.stdlib.expr;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.lib.stdlib.ChainResolveFw;
import org.fw.lib.stdlib.WrapperTypeFw;
import org.fw.lib.stdlib.constraint.ConstraintFw;

import static org.fw.core.FW.symbol;

public final class SyntaxResolveFw {

    public static final Type syntaxResolve = WrapperTypeFw.wrapperType(ChainResolveFw.chainResolveType(ExprFw.isExpr),
            FW.telephonist(instance -> FW.telephonist(rawPayload -> FW.telephonist(arg -> {
                if (arg.type() == SymbolFw.symbol) {
                    String s = arg._unpack().toString();
                    switch (s) {
                        case "expr":
                            return rawPayload.call(symbol("passing"));
                        case "comp-env":
                            return rawPayload.call(symbol("chain"));
                    }
                }
                return null;
            }))), FW.telephonist(arg -> {
                return null;
            }));

    public static final Type toExprResolve = ChainResolveFw.chainResolveType(ConstraintFw.isSpecified);

    public static final Val syntaxResolveToExpr = FW.telephonist((arg) -> {
        if (arg.type() != ToExprFn.toExprResolve)
            return null;
        Val toExpr = arg.call(symbol("chain"));
        arg = arg.call(symbol("passing"));

        Type type = arg.type();
//        if (type.equals(syntaxResolve)) {
//            return ExprFw.wrap(arg._unpack(SyntaxResolve.class).toExpr(RtEnv.unspecified));
//        }
        return null;
    });

}
