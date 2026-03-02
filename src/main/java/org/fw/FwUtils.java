package org.fw;

import org.fw.ast.BracketsTypes;
import org.fw.ast.Expr;
import org.fw.ast.Symbol;
import org.fw.ast.lexer.ExprOutput;
import org.fw.ast.lexer.TokenOutput;
import org.fw.base.*;
import org.fw.lib.DeclaredFw;
import org.fw.lib.VitFw;
import org.fw.lib.comp.*;
import org.fw.lib.expr.CompEnv;
import org.fw.lib.expr.ExprFw;
import org.fw.vit.Vit;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

import static org.fw.vit.Vit.val;

public final class FwUtils {
    private FwUtils() throws InstantiationException
        { throw new InstantiationException(); }


    public static Val handleSymbols(Val arg, Type type, Context context, SHandler handler, TelephonistType.CallFunction orStatic) {
        return handleSymbols(arg, type, context, handler, (instance, arg1) -> Val.unspecified, orStatic);
    }

    public static Val handleSymbols(Val arg, Type type, Context context, SHandler handler, NSHandler nonSymbolicHandler, TelephonistType.CallFunction orStatic) {
        if (isTypeApiCall(arg, type, context)) {
            Val instance = Call.getVal(arg, context);
            Val callArg = Call.getArg(arg, context);
            if (!callArg.type().equals(ExprFw.symbol)) {
                return nonSymbolicHandler.handle(instance, callArg);
            }
            String symbol = callArg._unpack(Symbol.class).getValue();
            return handler.handle(instance, symbol);
        }
        return orStatic.call(arg, context);
    }

    public static Expr parse(String name) {
        return new ExprOutput(new TokenOutput(name, null, BracketsTypes.bracketsTypes)).iterator().next();
    }

    public static boolean isTypeApiCall(Val call, Type type, Context context) {
        if (call.type().equals(Call.call_t)) {
            Val val = Call.getVal(call, context);
            return val.type().equals(type);
        }
        return false;
    }

    public static Val getValueFromFile(File file, CompEnv compEnv, Context context) throws IOException {
        Iterable<Expr> expressions = new ExprOutput(new TokenOutput(file, BracketsTypes.bracketsTypes));
        Val result = Val.unspecified;


        Map<String, Val> defineds = new HashMap<>();

        final Val defined = InternalSymbolMapCEnvFw.symbolMapVitEnv(val(FW.telephonist("vals", (arg1, _) -> {
            if (!arg1.type().equals(ExprFw.symbol))
                return Val.unspecified;
            String string = arg1._unpack().toString();
            Val ret = defineds.get(string);
            if (ret != null)
                return VitFw.wrap(val(ret));
            return Val.unspecified;
        })));


        CompEnv env = CompEnv.of(CompEnv.compEnv(context, compEnv.asVal(), defined));

        for (Expr expr : expressions) {
            Vit vit = env.compile(expr, context);
            if (vit == null) {
                throw new IOException("Cannot compile: " + expr);
            }
            result = vit.eval(context);
            if (result.type().equals(DeclaredFw.declared)) {
                Val key = DeclaredFw.getKey(result, context);
                Val value = DeclaredFw.getValue(result, context);
                if (key.type().equals(ExprFw.symbol)) {
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
