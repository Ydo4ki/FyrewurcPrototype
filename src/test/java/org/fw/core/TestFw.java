package org.fw.core;

import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.base.*;
import org.fw.core.lib.DIntFw;
import org.fw.core.lib.VitFw;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.ExprCallOpFw;
import org.fw.core.lib.expr.ExprFw;
import org.fw.core.state.obj.State;
import org.fw.core.util.FwUtils;
import org.fw.core.base.context.RtEnv;
import org.fw.core.vit.Vit;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.fw.core.FW.symbol;

public final class TestFw {
    public static final Type test = FW.telephonist("Test", (arg) -> {
        if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"));
            Val cEnv = arg.call(symbol("comp-env"));

            int isize = size._unpack(BigInteger.class).intValue();

            Vit[] vits = new Vit[isize];
            for (int i = 0; i < isize; i++) {
                Val argNVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(i))._unpack(), CompEnv.of(cEnv)));
                if (!VitFw.isVit(argNVit.type()))
                    return argNVit; // compile error idk

                vits[i] = Vit.simplify(argNVit._unpack(Vit.class));
            }
            return VitFw.wrap(Vit.val(Val.of(TestFw.test, new TestRecord(vits))).call(symbol("complete")).call(Vit.var)); // nah
        } else if (FwUtils.isTypeApiCall(arg, TestFw.test)) {
            TestRecord instance = Call.getVal(arg)._unpack();
            arg = Call.getArg(arg);
            if (arg.equals(symbol("complete"))) {
                return FW.telephonist("completion", (arg1) -> {
                    return State.performAndDie(state -> Val.of(TestFw.test, new TestRecord(instance.statements())));
                });
            }
        }
        return null;
    }).asType();

    public static final Val testToExpr = FW.telephonist((arg) -> {
        if (arg.type().equals(TestFw.test)) {
            TestRecord test = arg._unpack();

            List<Expr> exprs = new ArrayList<>();
            exprs.add(TestFw.test.asVal().toExpr(RtEnv.unspecified));
            for (Vit statement : test.statements()) {
                exprs.add(VitFw.wrap(statement).toExpr(RtEnv.unspecified));
            }
            return ExprFw.wrap(ExprList.of(BracketsTypes.round, exprs));
        }
        return null;
    });

    static final class TestRecord {
        private final Vit[] statements;

        TestRecord(Vit[] statements) {
            this.statements = statements;
        }

        public Vit[] statements() {
            return statements;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            TestRecord that = (TestRecord) obj;
            return Arrays.equals(this.statements, that.statements);
        }

        @Override
        public int hashCode() {
            return Objects.hash(Arrays.hashCode(statements));
        }

        @Override
        public String toString() {
            return "TestRecord[" +
                    "statements=" + Arrays.toString(statements);
        }
    }
}
