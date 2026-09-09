package org.fw.test;

import com.ydo4ki.esast.Expr;
import com.ydo4ki.esast.LocatedExpr;
import com.ydo4ki.esast.lexer.ExprOutput;
import com.ydo4ki.fw.internal.lib.stdlib.state.SystemOperation;
import org.fw.DirectCompEnv;
import org.fw.DirectVitCompilationException;
import org.fw.base.BoolFw;
import org.fw.base.Val;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;
import org.fw.core.vit.Vit;
import org.fw.std.DeclaredFw;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Tester {
    public static void testDirectFw(Class<?> cls, Val module) throws IOException {
        testDirectFw(cls, camelCaseTo_fw(cls.getSimpleName()) + ".dfw", module);
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
                    } catch (DirectVitCompilationException e) {
                        System.err.println(expression);
                        throw new RuntimeException(e);
                    }
                    val = (Val) vit.eval(FW.lambda((arg) -> null), state);
                    if (val.getType() == DeclaredFw.declared) {
                        defined.put(DeclaredFw.getKey(val)._UNPACK_().toString(), DeclaredFw.getValue(val));
                    } else if (val == BoolFw._false) {
                        throw new AssertionError(expression);
                    } else if (val != Operation.unit && val != BoolFw._true)
                        System.out.println(val);
//                        if (debug) System.out.println(val);
                }
                return val;
            }
        };
        op.apply(SystemOperation.systemState);
    }

    static String camelCaseTo_fw(String className) {
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
}
