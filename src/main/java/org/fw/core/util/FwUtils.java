package org.fw.core.util;

import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import org.fw.core.ast.*;
import org.fw.core.ast.lexer.ExprOutput;
import org.fw.core.ast.lexer.TokenOutput;
import org.fw.core.base.*;
import org.fw.core.base.BoolFw;
import org.fw.lib.stdlib.DeclaredFw;
import org.fw.core.base.TypeGetFw;
import org.fw.lib.stdlib.expr.Lib;
import org.fw.lib.stdlib.ModuleFw;
import org.fw.lib.stdlib.VitFw;
import org.fw.lib.stdlib.expr.CompEnv;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;
import org.fw.core.base.context.RtEnv;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;
import com.ydo4ki.fw.internal.lib.stdlib.state.SystemOperation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Predicate;

import static org.fw.core.FW.symbol;
import static org.fw.core.vit.Vit.val;

// todo: replace all java File with FwFiles or something like that so they won't be attached to the actual filesystem
public final class FwUtils {
    private FwUtils() throws InstantiationException
        { throw new InstantiationException(); }


    public static LocatedExpr<? extends Expr> parse(String name) {
        return new ExprOutput(new TokenOutput(name, null, BracketsTypes.bracketsTypes)).iterator().next();
    }

    public static boolean isTypeApiCall(Value call, Type type) {
        if (call.getType0().impliesEquality(CallFw.call_t.asVal())) {
            Value val = CallFw.getVal(call);
            return val.getType0().impliesEquality(type.asVal());
        }
        return false;
    }

    public static Vit isTypeApiCall(Vit call, Type type) {
        return EqFw.eq(Vit.call(TypeGetFw.typeGet, call), Vit.val(CallFw.call_t.asVal()))
                .call(symbol("and"))
                .call(EqFw.eq(Vit.call(TypeGetFw.typeGet, call.call(symbol("val"))), Vit.val(type.asVal())));
    }

    public static Val getValueFromFile(File file, CompEnv compEnv) throws IOException {
        return State.performAndDie(s -> {
            try {
                return getValueFromFile(file, compEnv, RtEnv.unspecified, s);
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

    public static Val getValueFromFile(File file, CompEnv compEnv, RtEnv rtEnv, State state) throws IOException {
        Iterable<LocatedExpr<? extends Expr>> expressions = new ExprOutput(new TokenOutput(file, BracketsTypes.bracketsTypes));
        Val result = Operation.unit; // this will be returned if the file is empty


        Map<String, Val> defineds = new HashMap<>();

        final Val defined = symbolMapVitEnv(val(FW.telephonist_native("vals", (arg1) -> {
            if (!arg1.getType().equals(SymbolFw.symbol))
                return null;
            String string = arg1._UNPACK().toString();
            Val ret = defineds.get(string);
            if (ret != null)
                return VitFw.wrap(val(ret));
            return null;
        })));


        CompEnv env = CompEnv.of(CompEnv.compEnv(compEnv.asVal(), defined));

        for (LocatedExpr<? extends Expr> lExpr : expressions) {
            Expr expr = lExpr.getExpr();
            Vit vit = null;
            try {
                vit = env.compile(expr);
            } catch (VitCompilationException e) {
                throw new RuntimeException("Cannot compile: " + expr, e);
            }
            result = vit.eval(rtEnv, state);
            if (result.getType().equals(DeclaredFw.declared)) {
                Val key = DeclaredFw.getKey(result);
                Val value = DeclaredFw.getValue(result);
                if (key.getType().equals(SymbolFw.symbol)) {
                    defineds.put(key._UNPACK(Symbol.class).getValue(), value);
                }
            }
        }
        return result;
    }

    public static <T> Set<T> mergeImmut(Set<T> a, Set<T> b) {
        if (a.isEmpty()) return b;
        if (b.isEmpty()) return a;
        Set<T> set = new HashSet<>(a);
        set.addAll(b);
        return set;
    }

    public static Val valify(Predicate<Val> tester) {
        return FW.telephonist_native((arg) -> BoolFw.wrap(tester.test(arg)));
    }

    public static Vit equals(Vit a, Vit b) {
        return val(EqFw.eq).call(a).call(b);
    }

    public static Val symbolMapVitEnv(Vit telemap) {
        Vit arg = Vit.var.call(FW.symbol("arg"));
        Vit argExpr = arg.call(symbol("expr"));
        Vit parseArg = telemap.call(argExpr);
        return FW.telephonist_native((arg1) -> {
            if (Unspecified.isUnspecified(arg1)) return null;
            else return parseArg.eval();
        });
//        return VitiateTelephonistFw.vitiate(
//                FW.vIf(val(eq).call(parseArg).call(null).call(symbol("not")),
//                        parseArg,
//                        val(null)
//                ), symbol("arg"), InternalSystemContext.context);
    }

    public static Operation getOperation(Class<?> cls, String filename, final CompEnv compEnv, boolean debug) throws IOException {
        return getOperation(cls.getPackage().getName().replace(".", "/") + "/" + filename, compEnv, debug);
    }

    public static Operation getOperation(String filename, final CompEnv compEnv, boolean debug) throws IOException {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename + ".fw");
        if (in == null)
            throw new IOException("Source not found: " + filename + ".fw");

        Iterable<LocatedExpr<? extends Expr>> expressions = ExprOutput.valueOf(in);
        return new Operation() {
            @Override
            public Val apply(State state) {
                CompEnv compEnv1 = compEnv;
                Val val = Operation.unit;
                for (LocatedExpr<? extends Expr> locatedExpression : expressions) {
                    Expr expression = locatedExpression.getExpr();
                    Vit vit;
                    try {
                        vit = compEnv1.compile(expression);
                    } catch (VitCompilationException e) {
                        System.err.println(expression);
                        throw new RuntimeException(e);
                    }
                    val = vit.eval(RtEnv.unspecified, state);
                    if (val.getType() == DeclaredFw.declared) {
                        compEnv1 = CompEnv.of(CompEnv.compEnv(compEnv1.asVal(), ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(val))));
                    } else if (val != Operation.unit)
                        if (debug) System.out.println(val.toExpr(compEnv));
                }
                return val;
            }
        };
    }

    public static Lib l(Class<?> caller, Lib lib0, String... files) {
        try {
            for (String file : files) {
                lib0 = Lib.combine(lib0,
                        Lib.ofCEnv(ModuleFw.ModuleCEnvFw.compEnv(
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

    @FunctionalInterface
    public interface SHandler {
        Val handle(Val instance, String symbol);
    }

    @FunctionalInterface
    public interface NSHandler {
        Val handle(Val instance, Val arg);
    }

    public interface BoolBinaryOperator {
        boolean apply(boolean a, boolean b);
    }

    public interface BigBinaryOperator {
        BigInteger apply(BigInteger a, BigInteger b);
    }
}
