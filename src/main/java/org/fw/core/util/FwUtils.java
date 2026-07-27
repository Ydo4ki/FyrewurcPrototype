package org.fw.core.util;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.LocatedExpr;
import org.fw.core.ast.Symbol;
import org.fw.core.ast.lexer.ExprOutput;
import org.fw.core.ast.lexer.TokenOutput;
import org.fw.core.base.*;
import org.fw.core.base.context.Context;
import org.fw.core.lib.BoolFw;
import org.fw.core.lib.DeclaredFw;
import org.fw.core.lib.VitFw;
import org.fw.core.lib.comp.InternalSymbolMapCEnvFw;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;
import org.fw.core.base.context.RtEnv;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Predicate;

import static org.fw.core.vit.Vit.val;

// todo: replace all java File with FwFiles or something like that so they won't be attached to the actual filesystem
public final class FwUtils {
    private FwUtils() throws InstantiationException
        { throw new InstantiationException(); }


    @Deprecated
    public static Val handleSymbols(Val arg, Type type, Context context, SHandler handler, TelephonistType.CallFunction orStatic) throws Exception {
        return handleSymbols(arg, type, context, handler, (instance, arg1) -> null, orStatic);
    }

    @Deprecated
    public static Val handleSymbols(Val arg, Type type, Context context, SHandler handler, NSHandler nonSymbolicHandler, TelephonistType.CallFunction orStatic) throws Exception {
        if (isTypeApiCall(arg, type, context)) {
            Val instance = Call.getVal(arg, context);
            Val callArg = Call.getArg(arg, context);
            if (!callArg.type().equals(SymbolFw.symbol)) {
                return nonSymbolicHandler.handle(instance, callArg);
            }
            String symbol = callArg._unpack(Symbol.class).getValue();
            return handler.handle(instance, symbol);
        }
        return orStatic.call(arg, context);
    }

    public static LocatedExpr<? extends Expr> parse(String name) {
        return new ExprOutput(new TokenOutput(name, null, BracketsTypes.bracketsTypes)).iterator().next();
    }

    public static boolean isTypeApiCall(Val call, Type type, Context context) {
        if (call.type().equals(Call.call_t)) {
            Val val = Call.getVal(call, context);
            return val.type().equals(type);
        }
        return false;
    }

    public static Val getValueFromFile(File file, CompEnv compEnv) throws IOException {
        return State.performAndDie(s -> {
            try {
                return getValueFromFile(file, compEnv, new Context(RtEnv.unspecified, s));
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

    public static Val getValueFromFile(File file, CompEnv compEnv, Context context) throws IOException {
        Iterable<LocatedExpr<? extends Expr>> expressions = new ExprOutput(new TokenOutput(file, BracketsTypes.bracketsTypes));
        Val result = Operation.unit; // this will be returned if the file is empty


        Map<String, Val> defineds = new HashMap<>();

        final Val defined = InternalSymbolMapCEnvFw.symbolMapVitEnv(val(FW.telephonist("vals", (arg1, с) -> {
            if (!arg1.type().equals(SymbolFw.symbol))
                return null;
            String string = arg1._unpack().toString();
            Val ret = defineds.get(string);
            if (ret != null)
                return VitFw.wrap(val(ret));
            return null;
        })));


        CompEnv env = CompEnv.of(CompEnv.compEnv(context, compEnv.asVal(), defined));

        for (LocatedExpr<? extends Expr> lExpr : expressions) {
            Expr expr = lExpr.getExpr();
            Vit vit = null;
            try {
                vit = env.compile(expr, context);
            } catch (VitCompilationException e) {
                throw new RuntimeException("Cannot compile: " + expr, e);
            }
            result = vit.eval(context);
            if (result.type().equals(DeclaredFw.declared)) {
                Val key = DeclaredFw.getKey(result, context);
                Val value = DeclaredFw.getValue(result, context);
                if (key.type().equals(SymbolFw.symbol)) {
                    defineds.put(key._unpack(Symbol.class).getValue(), value);
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
        return FW.telephonist((arg, context) -> BoolFw.wrap(tester.test(arg)));
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
