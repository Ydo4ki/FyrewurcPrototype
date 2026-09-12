package org.fw.esast.expr;

import org.fw.base.*;
import org.fw.core.FW;

import com.ydo4ki.fw.internal.lib.ConstraintFw;
import org.fw.std.*;

import static org.fw.core.FW.symbol;

public final class SyntaxResolveFw {

    public static final Type syntaxResolve = WrapperTypeFw.wrapperType(ChainResolveFw.chainResolveType(ExprFw.isExprBugged),
            FW.lambda_native(instance -> FW.lambda_native(rawPayload -> FW.lambda_native(arg -> {
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
            }))), FW.lambda_native(arg -> {
                if (arg.equalsSymbol("builder")) return FW.lambda_native(arg1 -> {
                    return FW.lambda_native(arg2 -> {
                        return Val._NEW_INSTANCE_(SyntaxResolveFw.syntaxResolve, new ChainResolveFw.ChainResolve(arg1, arg2));
                    });
                });
                return null;
            }));

    private static final Type crtcis = ChainResolveFw.chainResolveType(ConstraintFw.isSpecified);

//    public static final Type toExprResolve = crtcis;

    public static final Type toExprResolve = WrapperTypeFw.wrapperType(crtcis,
            FW.lambda_native(instance -> FW.lambda(rawPayload -> FW.lambda(arg -> rawPayload.call(arg)))), FW.lambda(arg -> {
                if (arg.equalsSymbol("builder")) {
                    return FW.lambda_native((passingArg) -> {
                        Val val = ((Val) ConstraintFw.isSpecified.call(symbol("check")));
                        if (val.call(passingArg) != BoolFw._true)
                            return null;

                        return FW.lambda_native((chain) -> {
                            return Val._NEW_INSTANCE_(SyntaxResolveFw.toExprResolve, new ChainResolveFw.ChainResolve(passingArg, chain));
                        });
                    });
                }
                return null;
            }));
    public static final Type toFnResolve = WrapperTypeFw.wrapperType(crtcis,
            FW.lambda_native(instance -> FW.lambda(rawPayload -> FW.lambda(arg -> rawPayload.call(arg)))), FW.lambda(arg -> {
                if (arg.equalsSymbol("builder")) {
                    return FW.lambda_native((passingArg) -> {
                        Val val = ((Val) ConstraintFw.isSpecified.call(symbol("check")));
                        if (val.call(passingArg) != BoolFw._true)
                            return null;

                        return FW.lambda_native((chain) -> {
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
