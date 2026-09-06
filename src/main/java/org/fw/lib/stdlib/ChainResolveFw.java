package org.fw.lib.stdlib;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.util.FwUtils;

public final class ChainResolveFw {
    public static final Type chainResolveType = FW.telephonist_native("ChainResolveType", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, ChainResolveFw.chainResolveType)) {
            Val instance = (Val) CallFw.getVal(arg);
            arg = (Val) CallFw.getArg(arg);

            Type type = instance.asType();
            Val constraint = instance._UNPACK();
            if (FwUtils.isTypeApiCall(arg, type)) {
                instance = (Val) CallFw.getVal(arg);
                arg = (Val) CallFw.getArg(arg);

                ChainResolve cr = instance._UNPACK();
                if (arg.equalsSymbol("passing")) {
                    return cr.passing();
                } else if (arg.equalsSymbol("chain")) {
                    return cr.chain();
                }
            } else if (arg.equalsSymbol("builder")) {
                return FW.telephonist_native((passingArg) -> {
                    if (constraint.get("check").call(passingArg) != BoolFw._true)
                        return null;

                    return FW.telephonist_native((chain) -> {
                        return Val.of(type, new ChainResolve(passingArg, chain));
                    });
                });
            }
            return null;
        } else if (arg.equalsSymbol("builder")) {
            return FW.telephonist_native((constraint) -> {
                if (!ConstraintFw.isConstraint(constraint))
                    return null;

                return chainResolveType(constraint).asVal();
            });
        }
        return null;
    }).asType();

    public static Type chainResolveType(Val constraint) {
        return Val.of(ChainResolveFw.chainResolveType, constraint).asType();
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
