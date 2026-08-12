package org.fw.core.lib.expr;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.lib.ChainResolveFw;
import org.fw.core.lib.WrapperTypeFw;

import static org.fw.core.FW.symbol;

public final class SyntaxResolveFw {

    public static final Type syntaxResolve = WrapperTypeFw.wrapperType(ChainResolveFw.chainResolveType(ExprFw.isExpr),
            FW.telephonist(rawPayload -> FW.telephonist(arg -> {
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
