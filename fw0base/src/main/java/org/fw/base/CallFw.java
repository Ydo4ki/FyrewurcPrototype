package org.fw.base;

import org.fw.core.FW;
import org.fw.core.abstrait.Value;

import java.util.Objects;

import static org.fw.core.FW.*;

// Remember local runtimes
// what
// what is local runtimes
// what do i need to remember
// aaioasopdiou when was this even written
public final class CallFw {

    public static final Type call_t = telephonist_native("Call", (d) -> {
        Val arg = d.arg();
        if (arg.getType().equals(CallFw.call_t)) {
            // native
            DefinitiveValEnv<Value> call = d.unpack(arg);
            Val me = call.self();
            Value cArg = call.arg();
            DefinitiveValEnv<Value> meCall = d.unpack(me);
            if (cArg.equalsSymbol("arg")) return meCall.arg();
            if (cArg.equalsSymbol("val")) return meCall.self();
            if (cArg.equalsSymbol("instance")) return meCall.instancer();
            if (cArg.equalsSymbol("unpack")) return meCall.unpacker();
        }
        return null;
    }).asType();

    private static final Val construct = FW.lambda_native("Call.construct",
            (func) -> FW.lambda(
                    (argument) -> Val.of(call_t, new DefinitiveValEnv<>(func, argument))));

    static Value fwCall(Value instance, Value arg) {
        return construct.call(instance).call(arg);
    }

    @SuppressWarnings("unchecked")
    public static <V extends Value> DefinitiveValEnv<V> unwrap0(Val call) {
        return (DefinitiveValEnv<V>) call.getValue();
    }

    public static Value getVal(Value call) {
        return call.get("val");
    }

    public static Value getArg(Value call) {
        return call.get("arg");
    }
}
