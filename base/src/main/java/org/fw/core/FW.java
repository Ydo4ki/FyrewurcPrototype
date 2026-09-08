package org.fw.core;

import org.fw.base.SymbolFw;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.core.vit.Vit;

import static org.fw.base.EqFw.eq;
import static org.fw.core.vit.Vit.val;

public final class FW {

    public static Val telephonist(String name, Type.TelephonistType.CallFunction call) {
        return Val._NEW_INSTANCE_(Val.ofTelephonist(0).asType(), new Type.TelephonistType.Telephonist(name, call, s -> null));
    }

    public static Val telephonist(Type.TelephonistType.CallFunction call) {
        return telephonist(null, call);
    }

    public static Val telephonist_native(String name, Type.TelephonistType.NativeCallFunction call) {
        return telephonist(name, dve -> {
            try {
                return call.call(dve.recast());
            } catch (NativeExecutionException e) {
                throw e;
            } catch (Exception e) {
                throw new NativeExecutionException(e);
            }
        });
    }

    @Deprecated
    public static Val telephonist_native(Type.TelephonistType.NativeCallFunction call) {
        return telephonist_native(null, call);
    }

    @Deprecated
    public static Val lambda(String name, Type.TelephonistType.LambdaCallFunction call) {
        return telephonist(name, dve -> call.call(dve.arg()));
    }

    @Deprecated
    public static Val lambda(Type.TelephonistType.LambdaCallFunction call) {
        return lambda(null, call);
    }

    @Deprecated
    public static Val lambda_native(String name, Type.TelephonistType.NativeLambdaCallFunction call) {
        return lambda(name, arg -> {
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
    public static Val lambda_native(Type.TelephonistType.NativeLambdaCallFunction call) {
        return lambda_native(null, call);
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

    public static Val payloadConstraint(Type type) {
        throw new UnsupportedOperationException();
    }
}
