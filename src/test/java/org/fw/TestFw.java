package org.fw;

import org.fw.ast.BracketsTypes;
import org.fw.ast.Expr;
import org.fw.ast.ExprList;
import org.fw.base.Call;
import org.fw.base.Context;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.lib.BoxFw;
import org.fw.lib.DIntFw;
import org.fw.lib.VitFw;
import org.fw.lib.expr.CompEnv;
import org.fw.lib.expr.ExprCallOpFw;
import org.fw.lib.expr.ExprFw;
import org.fw.vit.RtEnv;
import org.fw.vit.Vit;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.fw.FW.symbol;

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
        } else if (arg.type().equals(ExprFw.toExpr)) {
            Val instance = BoxFw.unbox(arg);
            if (!instance.type().equals(TestFw.test))
                return Val.unspecified;

            TestRecord test = instance._unpack();

            List<Expr> exprs = new ArrayList<>();
            exprs.add(TestFw.test.asVal().toExpr(context));
            for (Vit statement : test.statements()) {
                exprs.add(VitFw.wrap(statement).toExpr(context));
            }
            return ExprFw.wrap(ExprList.of(BracketsTypes.round, exprs));
        }
        return Val.unspecified;
    }).asType();

    record TestRecord(Vit[] statements, Context context) {}
}
