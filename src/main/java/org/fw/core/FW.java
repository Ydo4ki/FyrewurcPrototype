package org.fw.core;

import org.fw.core.ast.Expr;
import org.fw.core.ast.Symbol;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.TelephonistType;
import org.fw.core.base.Val;
import org.fw.core.util.FwUtils;
import org.fw.core.util.LazyObj;
import org.fw.core.vit.Vit;

import java.util.function.Supplier;

import static org.fw.core.base.ValsFw.eq;
import static org.fw.core.vit.Vit.val;

public final class FW {
    // I changed my mind (partially)
    public static Val telephonist(String name, TelephonistType.CallFunction call) {
        return telephonistE(() -> FwUtils.parse(name).getExpr(), call);
    }

    public static Val telephonist(TelephonistType.CallFunction call) {
        return Val.of(Val.ofTelephonist(0).asType(), new TelephonistType.Telephonist(null, call));
    }

    public static Val telephonist(Supplier<String> name, TelephonistType.CallFunction call) {
        return telephonistE(() -> FwUtils.parse(name.get()).getExpr(), call);
    }

    public static Val telephonist(Expr representation, TelephonistType.CallFunction call) {
        return Val.of(Val.ofTelephonist(0).asType(), new TelephonistType.Telephonist(() -> representation, call));
    }

    public static Val telephonistE(Supplier<Expr> representation, TelephonistType.CallFunction call) {
//        System.out.println("# New Telephonist: " + representation);
        return Val.of(Val.ofTelephonist(0).asType(), new TelephonistType.Telephonist(LazyObj.of(representation), call));
    }

    public static Val symbol(String value) {
        return Val.of(SymbolFw.symbol, Symbol.of(value));
    }

    public static Vit vIf(Vit condition, Vit ifTrue, Vit ifFalse) {
        return condition.call(symbol("if"))
                .call(ifTrue)
                .call(ifFalse);
    }

    public static Vit vEq(Vit a, Vit b) {
        return val(eq).call(a).call(b);
    }
}
