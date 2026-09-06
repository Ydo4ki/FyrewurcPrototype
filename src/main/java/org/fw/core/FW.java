package org.fw.core;

import org.fw.core.base.*;
import org.fw.lib.stdlib.TypePayloadInfo;
import org.fw.core.vit.Vit;

import static org.fw.core.base.EqFw.eq;
import static org.fw.core.vit.Vit.val;

public final class FW {

    public static Val telephonist(String name, Type.TelephonistType.CallFunction call) {
        return Val.of(Val.ofTelephonist(0).asType(), new Type.TelephonistType.Telephonist(name, call));
    }

    public static Val telephonist(Type.TelephonistType.CallFunction call) {
        return telephonist(null, call);
    }

    public static Val telephonist_native(String name, Type.TelephonistType.NativeCallFunction call) {
        return telephonist(name, arg -> {
            try {
                return call.call((Val)arg);
            } catch (Exception e) {
                throw new NativeExecutionException(e);
            }
        });
    }

    @Deprecated
    public static Val telephonist_native(Type.TelephonistType.NativeCallFunction call) {
        return telephonist_native(null, call);
    }

    public static Val symbol(String value) {
        return Val.of(SymbolFw.symbol, value);
    }

    public static Vit vIf(Vit condition, Vit ifTrue, Vit ifFalse) {
        return condition.call(symbol("if"))
                .call(ifTrue)
                .call(ifFalse);
    }

    public static Vit vEq(Vit a, Vit b) {
        return val(eq).call(a).call(b);
    }

    public static Type payloadType(Type type) {
        Val ret = type.asVal().get("Payload");
        return TypePayloadInfo.value(ret);
    }

    public static Val payloadConstraint(Type type) {
        throw new UnsupportedOperationException();
    }
}
