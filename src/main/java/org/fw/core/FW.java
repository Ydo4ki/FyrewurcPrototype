package org.fw.core;

import org.fw.core.abstrait.TypedValue;
import org.fw.core.abstrait.Value;
import org.fw.core.base.*;
import org.fw.lib.stdlib.TypePayloadInfo;
import org.fw.core.vit.Vit;

import static org.fw.core.base.EqFw.eq;
import static org.fw.core.vit.Vit.val;

public final class FW {

    public static Val telephonist(String name, Type.TelephonistType.CallFunction call) {
        return Val._NEW_INSTANCE_(Val.ofTelephonist(0).asType(), new Type.TelephonistType.Telephonist(name, call));
    }

    public static Val telephonist(Type.TelephonistType.CallFunction call) {
        return telephonist(null, call);
    }

    public static Value telephonist_native(Type.TelephonistType.NativeCallFunction call) {
        return telephonist_native(null, call);
    }

    public static Value telephonist_native(String name, Type.TelephonistType.NativeCallFunction call) {
        return new TypedValue() {

            @Override
            public Value call(Value value) {
                if (!(value instanceof Val))
                    return null;
                try {
                    return call.call((Val) value);
                } catch (NativeExecutionException e) {
                    throw e;
                } catch (Exception e) {
                    throw new NativeExecutionException(e);
                }
            }

            @Override
            public Type getType() {
                return Val.ofTelephonist(0).asType();
            }

            @Override
            public boolean impliesEquality(Val val) {
                return false;
            }

            @Override
            public String toString() {
                return name == null ? super.toString() : name;
            }
        };
    }

    public static Val telephonist_native_standalone(String name, Type.TelephonistType.StandaloneNativeCallFunction call) {
        return telephonist(name, arg -> {
            try {
                return call.call((Val)arg);
            } catch (NativeExecutionException e) {
                throw e;
            } catch (Exception e) {
                throw new NativeExecutionException(e);
            }
        });
    }

    @Deprecated
    public static Val telephonist_native_standalone(Type.TelephonistType.StandaloneNativeCallFunction call) {
        return telephonist_native_standalone(null, call);
    }

    public static Val symbol(String value) {
        return Val._NEW_INSTANCE_(SymbolFw.symbol, value);
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
