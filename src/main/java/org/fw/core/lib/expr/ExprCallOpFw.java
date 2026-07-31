package org.fw.core.lib.expr;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.base.context.Context;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.lib.DIntFw;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.fw.core.FW.symbol;

// ExprCallOpFw
// you're so deprecated
// you're the deprecatiest class I've ever seen
// how on the earth are you still here
// even your freaking toString is deprecated
@Deprecated
public final class ExprCallOpFw {
    @Deprecated
    public static final Type exprCallOp = FW.telephonist("ExprCallOp", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, ExprCallOpFw.exprCallOp)) {
            Val val = Call.getVal(arg);
            Val cArg = Call.getArg(arg);

            ExprCallOp payload = val._unpack();
            if (cArg.equals(symbol("comp-env"))) {
                return payload.compEnv().asVal();
            } else if (cArg.equals(symbol("size"))) {
                return DIntFw.dint(payload.args.length);
            } else if (cArg.type().equals(DIntFw.dint)) {
                BigInteger index = DIntFw.unwrap0(cArg);
                if (index.bitLength() > 31)
                    return null; // out of bounds

                int i = index.intValue();
                if (i < 0 || i >= payload.args.length)
                    return null; // out of bounds

                return ExprFw.wrap(payload.args[i]);
            }
        } else if (arg.equals(symbol("of-expr-list"))) {
            return FW.telephonist("ExprCallOp.of-expr-list", (arg1) -> {
                if (arg1.type().equals(ExprFw.exprList)) {
                    ExprList list = arg1._unpack();
                    Expr[] args = new Expr[list.size()-1];
                    for (int i = 0; i < args.length; i++) {
                        args[i] = list.get(i + 1);
                    }
                    return FW.telephonist((cEnv) -> {
                        return Val.of(ExprCallOpFw.exprCallOp, new ExprCallOp(args, CompEnv.of(cEnv)));
                    });
                } else {
                    return null;
                }
            });
        }
        return null;
    }).asType();

    @Deprecated
    private static final class ExprCallOp {
        @Deprecated
        private final Expr[] args;
        @Deprecated
        private final CompEnv compEnv;

        @Deprecated
        private ExprCallOp(Expr[] args, CompEnv compEnv) {
            this.args = args;
            this.compEnv = compEnv;
        }

        @Deprecated
        public Expr[] args() {
            return args;
        }

        @Deprecated
        public CompEnv compEnv() {
            return compEnv;
        }

        @Deprecated
        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            ExprCallOp that = (ExprCallOp) obj;
            return Arrays.equals(this.args, that.args) &&
                    Objects.equals(this.compEnv, that.compEnv);
        }

        @Deprecated
        @Override
        public int hashCode() {
            return Objects.hash(Arrays.hashCode(args), compEnv);
        }

        @Deprecated
        @Override
        public String toString() {
            return "ExprCallOp[" +
                    "args=" + Arrays.toString(args) + ", " +
                    "compEnv=" + compEnv + ']';
        }
    }
}
