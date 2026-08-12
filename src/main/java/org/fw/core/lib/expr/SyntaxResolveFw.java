package org.fw.core.lib.expr;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.lib.ChainResolveFw;
import org.fw.core.lib.WrapperTypeFw;

import static org.fw.core.FW.symbol;

public final class SyntaxResolveFw {

    public static final Type syntaxResolve = WrapperTypeFw.wrapperType(Val.of(ChainResolveFw.chainResolveType, ExprFw.constraint).asType(),
            FW.telephonist(rawPayload -> FW.telephonist(arg -> {
                if (arg.equals(symbol("expr"))) {
                    return rawPayload.call(symbol("passing"));
                } else if (arg.equals(symbol("comp-env"))) {
                    return rawPayload.call(symbol("chain"));
                }
                return null;
            })), FW.telephonist(arg -> {
                return null;
            }));
    public static final Val syntaxResolveToExpr = FW.telephonist((arg) -> {
        Type type = arg.type();
//        if (type.equals(syntaxResolve)) {
//            return ExprFw.wrap(arg._unpack(SyntaxResolve.class).toExpr(RtEnv.unspecified));
//        }
        return null;
    });

}
