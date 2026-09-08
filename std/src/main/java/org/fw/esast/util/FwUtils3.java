package org.fw.esast.util;

import com.ydo4ki.esast.*;
import com.ydo4ki.fw.internal.lib.stdlib.state.SystemOperation;
import org.fw.base.SymbolFw;
import org.fw.base.Val;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;
import org.fw.core.util.FwUtils;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCall;
import org.fw.esast.ExprVitCompilationException;
import org.fw.esast.expr.CompEnv;
import com.ydo4ki.esast.lexer.ExprOutput;
import com.ydo4ki.esast.lexer.TokenOutput;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

import org.fw.esast.expr.ExprFw;
import org.fw.std.DeclaredFw;
import org.fw.esast.expr.Lib;
import org.fw.std.ModuleFw;
import org.fw.std.VitFw;

import static org.fw.core.vit.Vit.val;

public final class FwUtils3 {
    public static LocatedExpr<? extends Expr> parse(String name) {
        return new ExprOutput(new TokenOutput(name, null, BracketsTypes.bracketsTypes)).iterator().next();
    }

    public static Val getValueFromFile(File file, CompEnv compEnv) throws IOException {
        return State.performAndDie(s -> {
            try {
                return getValueFromFile(file, compEnv, FW.lambda((arg) -> null), s);
            } catch (IOException e) {
                sneakyThrow(e);
                return null;
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void sneakyThrow(Throwable t) throws T {
        throw (T) t;
    }

    public static Val getValueFromFile(File file, CompEnv compEnv, Val rtEnv, State state) throws IOException {
        Iterable<LocatedExpr<? extends Expr>> expressions = new ExprOutput(new TokenOutput(file, BracketsTypes.bracketsTypes));
        Val result = Operation.unit; // this will be returned if the file is empty


        Map<String, Val> defineds = new HashMap<>();

        final Val defined = FwUtils.symbolMapVitEnv(val(FW.lambda_native("vals", (arg1) -> {
            if (!arg1.getType().equals(SymbolFw.symbol))
                return null;
            String string = arg1._UNPACK_().toString();
            Val ret = defineds.get(string);
            if (ret != null)
                return VitFw.wrap(val(ret));
            return null;
        })));


        CompEnv env = CompEnv.of(CompEnv.compEnv(compEnv.asValue(), defined));

        for (LocatedExpr<? extends Expr> lExpr : expressions) {
            Expr expr = lExpr.getExpr();
            Vit vit = null;
            try {
                vit = env.compile(expr);
            } catch (ExprVitCompilationException e) {
                throw new RuntimeException("Cannot compile: " + expr, e);
            }
            result = (Val) vit.eval(rtEnv, state);
            if (result.getType().equals(DeclaredFw.declared)) {
                Val key = (Val) DeclaredFw.getKey(result);
                Val value = (Val) DeclaredFw.getValue(result);
                if (key.getType().equals(SymbolFw.symbol)) {
                    defineds.put(((Symbol) ExprFw.unwrap(key)).getValue(), value);
                }
            }
        }
        return result;
    }

    public static Operation getOperation(Class<?> cls, String filename, final CompEnv compEnv, boolean debug) throws IOException {
        return getOperation(cls.getPackage().getName().replace(".", "/") + "/" + filename, compEnv, debug);
    }

    public static Operation getOperation(String filename, final CompEnv compEnv, boolean debug) throws IOException {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
        if (in == null)
            throw new IOException("Source not found: " + filename);

        Iterable<LocatedExpr<? extends Expr>> expressions = ExprOutput.valueOf(in);
        return new Operation() {
            @Override
            public Value apply(State state) {
                CompEnv compEnv1 = compEnv;
                Val val = Operation.unit;
                for (LocatedExpr<? extends Expr> locatedExpression : expressions) {
                    Expr expression = locatedExpression.getExpr();
                    Vit vit;
                    try {
                        vit = compEnv1.compile(expression);
                    } catch (ExprVitCompilationException e) {
                        System.err.println(expression);
                        throw new RuntimeException(e);
                    }
                    val = (Val) vit.eval(FW.lambda((arg) -> null), state);
                    if (val.getType() == DeclaredFw.declared) {
                        compEnv1 = CompEnv.of(CompEnv.compEnv(compEnv1.asValue(), ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(val))));
                    } else if (val != Operation.unit)
                        if (debug) System.out.println(compEnv.toExpr(val));
//                        if (debug) System.out.println(val);
                }
                return val;
            }
        };
    }

    public static Lib l(Class<?> caller, Lib lib0, String... files) {
        try {
            for (String file : files) {
                lib0 = Lib.combine(lib0,
                        Lib.ofCEnv(ModuleFw.ModuleCEnvFw.compEnv((Val)
                                getOperation(caller, file, CompEnv.of(lib0.exports()), false)
                                        .apply(SystemOperation.systemState)))
                );
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return lib0;
    }

    public static void prettyPrintln(PrintStream out, Expr expr) {
        prettyPrint(out, expr, "");
        out.println();
    }

    public static void prettyPrint(PrintStream out, Expr expr, String tab) {
        if (expr instanceof Symbol) out.print(expr);
        else {
            ExprList list = (ExprList) expr;
            if (isFlat(list)) out.print(expr);
            else {
                out.print(list.getBracketsType().open());
                int i = 0;
                if (list.getBracketsType() == BracketsTypes.round) {
                    prettyPrint(out, list.get(i++), tab);
                }
                for (; i < list.size(); i++) {
                    Expr e = list.get(i);
                    out.println();
                    out.print(tab);
                    prettyPrint(out, e, tab + " ");
                }
                out.println();
                out.print(list.getBracketsType().close());
            }
        }
    }

    private static boolean isFlat(ExprList list) {
        for (Expr expr : list) {
            if (expr instanceof ExprList && ((ExprList) expr).size() > 1) return false;
        }
        return true;
    }

    public static Collection<? extends Expr> exprs(VitCall vitCall, CompEnv compEnv) {
        List<Expr> elements = new ArrayList<>();
        if (vitCall.func() instanceof VitCall) {
            elements.addAll(exprs(((VitCall) vitCall.func()), compEnv));
        } else {
            elements.add(compEnv.toExpr(VitFw.wrap(vitCall.func())));
        }
        elements.add(compEnv.toExpr(VitFw.wrap(vitCall.arg())));
        return elements;
    }
}
