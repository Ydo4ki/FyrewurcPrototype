package org.fw.test;

import com.ydo4ki.esast.*;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import org.fw.base.BoolFw;
import org.fw.base.Val;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;
import org.fw.esast.expr.ExprFw;
import org.fw.esast.util.FwUtils3;
import org.fw.esast.expr.CompEnv;
import org.fw.esast.expr.SyntaxResolveFw;
import org.fw.std.state.OperationLibFw;
import org.fw.core.vit.Vit;
import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import org.fw.std.VitFw;
import com.ydo4ki.fw.internal.lib.stdlib.state.SystemOperation;

import java.io.IOException;

public class Tester3 extends Tester {

    public static void testExprFw(Class<?> cls, CompEnv compEnv) throws IOException {
        testExprFw(cls, camelCaseTo_fw(cls.getSimpleName()) + ".fw", compEnv);
    }

    public static void testExprFw(Class<?> cls, String filename, CompEnv compEnv) throws IOException {
        Operation op = FwUtils3.getOperation(cls, filename, CompEnv.of(CompEnv.compEnv(
                compEnv.asValue(),
                testDirectivesCenv.asValue()
        )), true);
        op.apply(SystemOperation.systemState);
    }


    public static final CompEnv testDirectivesCenv = CompEnv.of(FW.lambda_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = (Val) arg.call(FW.symbol("expr"));
            Val compEnv = (Val) arg.call(FW.symbol("comp-env"));
            Expr expr = ExprFw.unwrap(exprVal);
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) if (((Symbol) f).getValue().equals("assert!")) {
                    if (isize != 2)
                        return null;

                    Val val = ((Val) exprVal.call(DIntFw.dint(1)));
                    Val condition = (Val) compEnv.call(CompEnv.syntaxResolve(ExprFw.unwrap(val), CompEnv.of(compEnv)));
                    if (!VitFw.isVit(condition.getType()))
                        return condition;
                    Vit vitOperation = Vit.call(OperationLibFw._VitOperation, condition).call(Vit.var);
                    Vit assertOperation = Vit.call(FW.lambda_native(arg1 ->
                            new AssertOperation((Operation)arg1._UNPACK_()).asVal()), vitOperation);
                    return VitFw.wrap(Vit.invoke(assertOperation));
                }
            }
        }
        return null;
    }));

    public static class AssertOperation extends Operation {
        private final Operation _assert;

        AssertOperation(Operation anAssert) {
            _assert = anAssert;
        }

        @Override
        public Value apply(State state) {
            Value ret = _assert.apply(state);
            if (ret == BoolFw._true) return Operation.unit;
            else throw new AssertionError(_assert + " -> " + ret);
        }
    }
}
