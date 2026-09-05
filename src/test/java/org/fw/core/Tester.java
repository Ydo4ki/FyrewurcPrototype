package org.fw.core;

import org.fw.core.ast.*;
import org.fw.core.base.BoolFw;
import org.fw.core.base.Val;
import org.fw.core.base.contract.InvokeContract;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;
import org.fw.lib.stdlib.state.OperationFw;
import org.fw.core.util.FwUtils;
import org.fw.core.vit.Vit;
import org.fw.lib.stdlib.DIntFw;
import org.fw.lib.stdlib.VitFw;
import org.fw.lib.stdlib.expr.CompEnv;
import org.fw.lib.stdlib.expr.SyntaxResolveFw;
import com.ydo4ki.fw.internal.lib.stdlib.state.SystemOperation;

import java.io.IOException;

import static org.fw.core.FW.symbol;

public final class Tester {
    public static void testFw(Class<?> cls, CompEnv compEnv) throws IOException {
        testFw(cls, camelCaseTo_fw(cls.getSimpleName()), compEnv);
    }

    public static void testFw(Class<?> cls, String filename, CompEnv compEnv) throws IOException {
        Operation op = FwUtils.getOperation(cls, filename, CompEnv.of(CompEnv.compEnv(
                compEnv.asVal(),
                testDirectivesCenv.asVal()
        )), true);
        op.apply(SystemOperation.systemState);
    }

    private static String camelCaseTo_fw(String className) {
        if (className.endsWith("Fw"))
            className = className.substring(0, className.length() - 2);

        StringBuilder result = new StringBuilder();
        char[] charArray = className.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (Character.isUpperCase(c)) {
                if (i != 0) result.append("-");
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }


    public static final CompEnv testDirectivesCenv = CompEnv.of(FW.telephonist((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"));
            Val compEnv = arg.call(symbol("comp-env"));
            Expr expr = exprVal._unpack(Expr.class);
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) if (((Symbol) f).getValue().equals("assert!")) {
                    if (isize != 2)
                        return null;

                    Val condition = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1))._unpack(Expr.class), CompEnv.of(compEnv)));
                    if (!VitFw.isVit(condition.getType()))
                        return condition;
                    Vit vitOperation = Vit.call(OperationFw._VitOperation, condition).call(Vit.var);
                    Vit assertOperation = Vit.call(FW.telephonist(arg1 ->
                            new AssertOperation(arg1._unpack(Operation.class)).asVal()), vitOperation);
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
        public Val apply(State state) {
            Val ret = _assert.apply(state);
            if (ret == BoolFw._true) return Operation.unit;
            else throw new AssertionError(_assert + " -> " + ret);
        }

        @Override
        public InvokeContract contract() {
            return InvokeContract.unknown();
        }
    }
}
