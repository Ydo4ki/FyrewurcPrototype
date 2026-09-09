package org.fw.std;

import org.fw.base.*;
import org.fw.core.FW;

import com.ydo4ki.fw.internal.lib.ConstraintFw;
import org.fw.core.util.FwUtils;

public final class ChainResolveFw {
    public static final Type chainResolveType = FW.lambda_native("ChainResolveType", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, ChainResolveFw.chainResolveType)) {
            Val instance = (Val) CallFw.getVal(arg);
            arg = (Val) CallFw.getArg(arg);

            Type type = instance.asType();
            Val constraint = instance._UNPACK_();
            if (FwUtils.isTypeApiCall(arg, type)) {
                instance = (Val) CallFw.getVal(arg);
                arg = (Val) CallFw.getArg(arg);

                ChainResolve cr = instance._UNPACK_();
                if (arg.equalsSymbol("passing")) {
                    return cr.passing();
                } else if (arg.equalsSymbol("chain")) {
                    return cr.chain();
                }
            } else if (arg.equalsSymbol("builder")) {
                return FW.lambda_native((passingArg) -> {
                    Val val = (Val) constraint.get("check");
                    if (val.call(passingArg) != BoolFw._true)
                        return null;

                    return FW.lambda_native((chain) -> {
                        return Val._NEW_INSTANCE_(type, new ChainResolve(passingArg, chain));
                    });
                });
            }
            return null;
        } else if (arg.equalsSymbol("builder")) {
            return FW.lambda_native((constraint) -> {
                if (!ConstraintFw.isConstraint(constraint))
                    return null;

                return chainResolveType(constraint).asVal();
            });
        }
        return null;
    }).asType();

    public static Type chainResolveType(Val constraint) {
        return Val._NEW_INSTANCE_(ChainResolveFw.chainResolveType, constraint).asType();
    }

    public static final class ChainResolve {
        private final Val passing;
        private final Val chain;

        public ChainResolve(Val passing, Val chain) {
            this.passing = passing;
            this.chain = chain;
        }

        public Val passing() {
            return passing;
        }

        public Val chain() {
            return chain;
        }
    }
}
