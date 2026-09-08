package org.fw.core;

import org.fw.DirectCompEnv;
import org.fw.core.abstrait.Value;
import org.fw.esast.extern.*;
import org.fw.esast.extern.lexer.ExprOutput;
import org.fw.base.BoolFw;
import org.fw.base.Val;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;
import org.fw.core.vit.VitCompilationException;
import org.fw.std.DeclaredFw;
import org.fw.esast.expr.CompEnv;
import org.fw.esast.expr.SyntaxResolveFw;
import org.fw.std.state.OperationFw;
import org.fw.core.util.FwUtils;
import org.fw.core.vit.Vit;
import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import org.fw.std.VitFw;
import com.ydo4ki.fw.internal.lib.stdlib.state.SystemOperation;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class Tester {
    public static void testDirectFw(Class<?> cls, Val module) throws IOException {
        testDirectFw(cls, camelCaseTo_fw(cls.getSimpleName()) + ".dfw", module);
    }

    public static void testExprFw(Class<?> cls, CompEnv compEnv) throws IOException {
        testExprFw(cls, camelCaseTo_fw(cls.getSimpleName()) + ".fw", compEnv);
    }

    public static void testDirectFw(Class<?> cls, String filename, Val module) throws IOException {
        String filename1 = cls.getPackage().getName().replace(".", "/") + "/" + filename;
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename1);
        if (in == null)
            throw new IOException("Source not found: " + filename1);

        Iterable<LocatedExpr<? extends Expr>> expressions = ExprOutput.valueOf(in);
        Operation op = new Operation() {
            @Override
            public Value apply(State state) {
                Map<String, Val> defined = new HashMap<>();
                Function<String, Val> get = s -> {
                    Val ret = defined.get(s);
                    if (ret == null) ret = (Val) module.call(FW.symbol(s));
                    return ret;
                };
                Val val = Operation.unit;
                for (LocatedExpr<? extends Expr> locatedExpression : expressions) {
                    Expr expression = locatedExpression.getExpr();
                    Vit vit;
                    try {
                        vit = DirectCompEnv.compile(expression, get);
                    } catch (VitCompilationException e) {
                        System.err.println(expression);
                        throw new RuntimeException(e);
                    }
                    val = (Val) vit.eval(FW.telephonist((arg) -> null), state);
                    if (val.getType() == DeclaredFw.declared) {
                        defined.put(DeclaredFw.getKey(val)._UNPACK_().toString(), DeclaredFw.getValue(val));
                    } else if (val == BoolFw._false) {
                        throw new AssertionError(expression);
                    } else if (val != Operation.unit && val != BoolFw._true)
                        System.out.println(val.toString());
//                        if (debug) System.out.println(val);
                }
                return val;
            }
        };
        op.apply(SystemOperation.systemState);
    }

    public static void testExprFw(Class<?> cls, String filename, CompEnv compEnv) throws IOException {
        Operation op = FwUtils.getOperation(cls, filename, CompEnv.of(CompEnv.compEnv(
                compEnv.asValue(),
                testDirectivesCenv.asValue()
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


    public static final CompEnv testDirectivesCenv = CompEnv.of(FW.telephonist_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = (Val) arg.call(FW.symbol("expr"));
            Val compEnv = (Val) arg.call(FW.symbol("comp-env"));
            Expr expr = exprVal._UNPACK_(Expr.class);
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) if (((Symbol) f).getValue().equals("assert!")) {
                    if (isize != 2)
                        return null;

                    Val condition = (Val) compEnv.call(CompEnv.syntaxResolve(((Val) exprVal.call(DIntFw.dint(1)))._UNPACK_(Expr.class), CompEnv.of(compEnv)));
                    if (!VitFw.isVit(condition.getType()))
                        return condition;
                    Vit vitOperation = Vit.call(OperationFw._VitOperation, condition).call(Vit.var);
                    Vit assertOperation = Vit.call(FW.telephonist_native(arg1 ->
                            new AssertOperation(arg1._UNPACK_(Operation.class)).asVal()), vitOperation);
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
