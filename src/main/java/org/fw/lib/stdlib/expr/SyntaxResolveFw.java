package org.fw.lib.stdlib.expr;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.lib.stdlib.*;

import static org.fw.core.FW.symbol;

public final class SyntaxResolveFw {

    public static final Type syntaxResolve = WrapperTypeFw.wrapperType(ChainResolveFw.chainResolveType(ExprFw.isExprBugged),
            FW.telephonist_native(instance -> FW.telephonist_native(rawPayload -> FW.telephonist_native(arg -> {
                if (arg.getType() == SymbolFw.symbol) {
                    String s = arg._UNPACK_().toString();
                    switch (s) {
                        case "expr":
                            return (Val) rawPayload.call(symbol("passing"));
                        case "comp-env":
                            return (Val) rawPayload.call(symbol("chain"));
                    }
                }
                return null;
            }))), FW.telephonist_native(arg -> {
                if (arg.equalsSymbol("builder")) return FW.telephonist_native(arg1 -> {
                    return FW.telephonist_native(arg2 -> {
                        return Val._NEW_INSTANCE_(SyntaxResolveFw.syntaxResolve, new ChainResolveFw.ChainResolve(arg1, arg2));
                    });
                });
                return null;
            }));

    private static final Type crtcis = ChainResolveFw.chainResolveType(ConstraintFw.isSpecified);

//    public static final Type toExprResolve = crtcis;

    public static final Type toExprResolve = WrapperTypeFw.wrapperType(crtcis,
            FW.telephonist_native(instance -> FW.telephonist_native(rawPayload -> FW.telephonist_native(arg -> (Val) rawPayload.call(arg)))), FW.telephonist_native(arg -> {
                if (arg.equalsSymbol("builder")) {
                    return FW.telephonist_native((passingArg) -> {
                        Val val = ((Val) ConstraintFw.isSpecified.call(symbol("check")));
                        if ((Val) val.call(passingArg) != BoolFw._true)
                            return null;

                        return FW.telephonist_native((chain) -> {
                            return Val._NEW_INSTANCE_(SyntaxResolveFw.toExprResolve, new ChainResolveFw.ChainResolve(passingArg, chain));
                        });
                    });
                }
                return null;
            }));
    public static final Type toFnResolve = WrapperTypeFw.wrapperType(crtcis,
            FW.telephonist_native(instance -> FW.telephonist_native(rawPayload -> FW.telephonist_native(arg -> (Val) rawPayload.call(arg)))), FW.telephonist_native(arg -> {
                if (arg.equalsSymbol("builder")) {
                    return FW.telephonist_native((passingArg) -> {
                        Val val = ((Val) ConstraintFw.isSpecified.call(symbol("check")));
                        if ((Val) val.call(passingArg) != BoolFw._true)
                            return null;

                        return FW.telephonist_native((chain) -> {
                            return Val._NEW_INSTANCE_(SyntaxResolveFw.toFnResolve, new ChainResolveFw.ChainResolve(passingArg, chain));
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

    public static final Lib lib = Lib.ofModule(
            ModuleFw.module(
                    DeclaredFw.declared(symbol("SyntaxResolve"), syntaxResolve)
            )
    );
}
