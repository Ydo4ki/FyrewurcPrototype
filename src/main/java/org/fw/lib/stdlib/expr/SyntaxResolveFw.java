package org.fw.lib.stdlib.expr;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.lib.stdlib.ChainResolveFw;
import org.fw.lib.stdlib.WrapperTypeFw;
import org.fw.lib.stdlib.ConstraintFw;

import static org.fw.core.FW.symbol;

public final class SyntaxResolveFw {

    public static final Type syntaxResolve = WrapperTypeFw.wrapperType(ChainResolveFw.chainResolveType(ExprFw.isExprBugged),
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

    private static final Type crtcis = ChainResolveFw.chainResolveType(ConstraintFw.isSpecified);

//    public static final Type toExprResolve = crtcis;

    public static final Type toExprResolve = WrapperTypeFw.wrapperType(crtcis,
            FW.telephonist(instance -> FW.telephonist(rawPayload -> FW.telephonist(rawPayload::call))), FW.telephonist(arg -> {
                if (arg.equals(symbol("builder"))) {
                    return FW.telephonist((passingArg) -> {
                        if (ConstraintFw.isSpecified.call(symbol("check")).call(passingArg) != BoolFw._true)
                            return null;

                        return FW.telephonist((chain) -> {
                            return Val.of(SyntaxResolveFw.toExprResolve, new ChainResolveFw.ChainResolve(passingArg, chain));
                        });
                    });
                }
                return null;
            }));
    public static final Type toFnResolve = WrapperTypeFw.wrapperType(crtcis,
            FW.telephonist(instance -> FW.telephonist(rawPayload -> FW.telephonist(rawPayload::call))), FW.telephonist(arg -> {
                if (arg.equals(symbol("builder"))) {
                    return FW.telephonist((passingArg) -> {
                        if (ConstraintFw.isSpecified.call(symbol("check")).call(passingArg) != BoolFw._true)
                            return null;

                        return FW.telephonist((chain) -> {
                            return Val.of(SyntaxResolveFw.toFnResolve, new ChainResolveFw.ChainResolve(passingArg, chain));
                        });
                    });
                }
                return null;
            }));

    // ehh we really need something like MarkedType that would do the same as its payload but contain something to distinguish
    static {
        if (toExprResolve.equals(toFnResolve))
            throw new AssertionError("RIP");
    }

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
