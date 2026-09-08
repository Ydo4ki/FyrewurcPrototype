package org.fw.core.util;

import org.fw.base.*;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import org.fw.core.vit.Vit;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Predicate;

import static org.fw.core.FW.symbol;
import static org.fw.core.vit.Vit.val;

public final class FwUtils {
    private FwUtils() throws InstantiationException
        { throw new InstantiationException(); }


    public static boolean isTypeApiCall(Value call, Type type) {
        if (call.getTypeValue().impliesEquality(CallFw.call_t.asVal())) {
            Value val = CallFw.getVal(call);
            return val.getTypeValue().impliesEquality(type.asVal());
        }
        return false;
    }

    public static Vit isTypeApiCall(Vit call, Type type) {
        return EqFw.eq(Vit.call(TypeGetFw.typeGet, call), Vit.val(CallFw.call_t.asVal()))
                .call(symbol("and"))
                .call(EqFw.eq(Vit.call(TypeGetFw.typeGet, call.call(symbol("val"))), Vit.val(type.asVal())));
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
        return FW.telephonist((arg1) -> {
            if (Unspecified.isUnspecified(arg1)) return null;
            else return parseArg.eval();
        });
//        return VitiateTelephonistFw.vitiate(
//                FW.vIf(val(eq).call(parseArg).call(null).call(symbol("not")),
//                        parseArg,
//                        val(null)
//                ), symbol("arg"), InternalSystemContext.context);
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
