package org.fw.lib.stdlib;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.lib.stdlib.constraint.ConstraintFw;
import org.fw.core.util.FwUtils;

import static org.fw.core.FW.symbol;

public final class ChainResolveFw {
    public static final Type chainResolveType = FW.telephonist("ChainResolveType", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, ChainResolveFw.chainResolveType)) {
            Val instance = CallFw.getVal(arg);
            arg = CallFw.getArg(arg);

            Type type = instance.asType();
            Val constraint = instance._unpack();
            if (FwUtils.isTypeApiCall(arg, type)) {
                instance = CallFw.getVal(arg);
                arg = CallFw.getArg(arg);

                ChainResolve cr = instance._unpack();
                if (arg.equals(symbol("passing"))) {
                    return cr.passing();
                } else if (arg.equals(symbol("chain"))) {
                    return cr.chain();
                }
            } else if (arg.equals(symbol("builder"))) {
                return FW.telephonist((passingArg) -> {
                    if (constraint.call(symbol("check")).call(passingArg) != BoolFw._true)
                        return null;

                    return FW.telephonist((chain) -> {
                        return Val.of(type, new ChainResolve(passingArg, chain));
                    });
                });
            }
            return null;
        } else if (arg.equals(symbol("builder"))) {
            return FW.telephonist((constraint) -> {
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
