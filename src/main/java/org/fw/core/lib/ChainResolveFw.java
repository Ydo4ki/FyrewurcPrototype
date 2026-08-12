package org.fw.core.lib;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.lib.expr.SyntaxResolveFw;
import org.fw.core.util.FwUtils;

import static org.fw.core.FW.symbol;

public final class ChainResolveFw {
    public static final Type chainResolveType = FW.telephonist("SyntaxResolve", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, ChainResolveFw.chainResolveType)) {
            Val instance = Call.getVal(arg);
            arg = Call.getArg(arg);

            Type type = instance.asType();
            Val constraint = instance._unpack();
            if (FwUtils.isTypeApiCall(arg, type)) {
                instance = Call.getVal(arg);
                arg = Call.getArg(arg);

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
        }
        return null;
    }).asType();

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
