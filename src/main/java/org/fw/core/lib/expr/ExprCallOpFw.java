package org.fw.core.lib.expr;

import org.fw.core.FW;
import org.fw.core.FwUtils;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.base.Call;
import org.fw.core.base.Context;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.DIntFw;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.fw.core.FW.symbol;

public final class ExprCallOpFw {
    public static final Type exprCallOp = FW.telephonist("ExprCallOp", (arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, ExprCallOpFw.exprCallOp, context)) {
            Val val = Call.getVal(arg, context);
            Val cArg = Call.getArg(arg, context);

            ExprCallOp payload = val._unpack();
            if (cArg.equals(symbol("comp-env"))) {
                return payload.compEnv().asVal();
            } else if (cArg.equals(symbol("size"))) {
                return DIntFw.dint(payload.args.length);
            } else if (cArg.type().equals(DIntFw.dint)) {
                BigInteger index = DIntFw.unwrap0(cArg);
                if (index.bitLength() > 31)
                    return Val.unspecified; // out of bounds

                int i = index.intValue();
                if (i < 0 || i >= payload.args.length)
                    return Val.unspecified; // out of bounds

                return ExprFw.wrap(payload.args[i]);
            }
        } else if (arg.equals(symbol("of-expr-list"))) {
            return FW.telephonist("ExprCallOp.of-expr-list", (arg1, context1) -> {
                if (arg1.type().equals(ExprFw.exprList)) {
                    ExprList list = arg1._unpack();
                    Expr[] args = new Expr[list.size()-1];
                    for (int i = 0; i < args.length; i++) {
                        args[i] = list.get(i + 1);
                    }
                    return FW.telephonist("(ExprCallOp.of-expr-list " + arg1.toExpr(context1) + ")", (cEnv, c) -> {
                        return Val.of(ExprCallOpFw.exprCallOp, new ExprCallOp(args, CompEnv.of(cEnv)));
                    });
                } else {
                    return Val.unspecified;
                }
            });
        }
        return Val.unspecified;
    }).asType();

    public static Val toExpr(Val arg, Context context) {
        ExprCallOpFw.ExprCallOp vec = arg._unpack(ExprCallOpFw.ExprCallOp.class);
        List<Expr> elements = new ArrayList<>();
        elements.add(ExprCallOpFw.exprCallOp.asVal().toExpr(context));
        for (Expr val : vec.args) {
            elements.add(ExprFw.wrap(val).toExpr(context));
        }
        return ExprFw.wrap(ExprList.of(BracketsTypes.round, elements));
    }

    private static final class ExprCallOp {
        private final Expr[] args;
        private final CompEnv compEnv;

        private ExprCallOp(Expr[] args, CompEnv compEnv) {
            this.args = args;
            this.compEnv = compEnv;
        }

        public Expr[] args() {
            return args;
        }

        public CompEnv compEnv() {
            return compEnv;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            ExprCallOp that = (ExprCallOp) obj;
            return Arrays.equals(this.args, that.args) &&
                    Objects.equals(this.compEnv, that.compEnv);
        }

        @Override
        public int hashCode() {
            return Objects.hash(Arrays.hashCode(args), compEnv);
        }

        @Override
        public String toString() {
            return "ExprCallOp[" +
                    "args=" + Arrays.toString(args) + ", " +
                    "compEnv=" + compEnv + ']';
        }
    }
}
