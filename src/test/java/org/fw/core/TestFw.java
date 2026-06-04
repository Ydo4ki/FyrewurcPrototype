package org.fw.core;

import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.base.Call;
import org.fw.core.base.Context;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.DIntFw;
import org.fw.core.lib.VitFw;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.ExprCallOpFw;
import org.fw.core.lib.expr.ExprFw;
import org.fw.core.vit.RtEnv;
import org.fw.core.vit.Vit;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.fw.core.FW.symbol;

public final class TestFw {
    public static final Type test = FW.telephonist("Test", (arg, context) -> {
        if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"), context);
            Val cEnv = arg.call(symbol("comp-env"), context);

            int isize = size._unpack(BigInteger.class).intValue();

            Vit[] vits = new Vit[isize];
            for (int i = 0; i < isize; i++) {
                Val argNVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(i), context)._unpack(), CompEnv.of(cEnv)), context);
                if (!VitFw.isVit(argNVit.type()))
                    return argNVit; // compile error idk

                vits[i] = Vit.simplify(argNVit._unpack(Vit.class), context);
            }
            return VitFw.wrap(Vit.val(Val.of(TestFw.test, new TestRecord(vits, null))).call(symbol("complete")).call(Vit.var)); // nah
        } else if (FwUtils.isTypeApiCall(arg, TestFw.test, context)) {
            TestRecord instance = Call.getVal(arg, context)._unpack();
            arg = Call.getArg(arg, context);
            if (arg.equals(symbol("complete"))) {
                return FW.telephonist("completion", (arg1, context1) -> {
                    return Val.of(TestFw.test, new TestRecord(instance.statements(), new Context(RtEnv.of(arg1), context1.scope())));
                });
            }
        }
        return Val.unspecified;
    }).asType();

    public static final Val testToExpr = FW.telephonist((arg, context) -> {
        if (arg.type().equals(TestFw.test)) {
            TestRecord test = arg._unpack();

            List<Expr> exprs = new ArrayList<>();
            exprs.add(TestFw.test.asVal().toExpr(context));
            for (Vit statement : test.statements()) {
                exprs.add(VitFw.wrap(statement).toExpr(context));
            }
            return ExprFw.wrap(ExprList.of(BracketsTypes.round, exprs));
        }
        return Val.unspecified;
    });

    static final class TestRecord {
        private final Vit[] statements;
        private final Context context;

        TestRecord(Vit[] statements, Context context) {
            this.statements = statements;
            this.context = context;
        }

        public Vit[] statements() {
            return statements;
        }

        public Context context() {
            return context;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            TestRecord that = (TestRecord) obj;
            return Arrays.equals(this.statements, that.statements) &&
                    Objects.equals(this.context, that.context);
        }

        @Override
        public int hashCode() {
            return Objects.hash(Arrays.hashCode(statements), context);
        }

        @Override
        public String toString() {
            return "TestRecord[" +
                    "statements=" + Arrays.toString(statements) + ", " +
                    "context=" + context + ']';
        }
    }
}
