package org.fw.core;

import org.fw.base.DefinitiveValEnv;
import org.fw.base.SymbolFw;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.core.abstrait.Value;
import org.fw.core.vit.Vit;

import java.util.Objects;

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
        return telephonist(name, new Ncf2Cv(call));
    }

    @Deprecated
    public static Val telephonist_native(Type.TelephonistType.NativeCallFunction call) {
        return telephonist_native(null, call);
    }


    public static Val lambda(String name, Type.TelephonistType.LambdaCallFunction call) {
        return telephonist(name, new Lcf2Nlcf(call));
    }

    public static Val lambda(Type.TelephonistType.LambdaCallFunction call) {
        return lambda(null, call);
    }

    public static Val lambda_native(String name, Type.TelephonistType.NativeLambdaCallFunction call) {
        return lambda(name, new Nlcf2Lcf(call));
    }

    private static class X2Y<X> {
        final X call;

        private X2Y(X call) {
            this.call = call;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            X2Y<?> x2Y = (X2Y<?>) o;
            return Objects.equals(call, x2Y.call);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(call);
        }
    }

    // we need those for equals to work properly
    private static class Ncf2Cv extends X2Y<Type.TelephonistType.NativeCallFunction> implements Type.TelephonistType.CallFunction {
        private Ncf2Cv(Type.TelephonistType.NativeCallFunction call) {
            super(call);
        }

        @Override
        public Value call(DefinitiveValEnv<Value> dve) {
            try {
                return call.call(dve.recast());
            } catch (NativeExecutionException e) {
                throw e;
            } catch (Exception e) {
                throw new NativeExecutionException(e);
            }
        }
    }

    private static class Lcf2Nlcf extends X2Y<Type.TelephonistType.LambdaCallFunction> implements Type.TelephonistType.CallFunction {
        private Lcf2Nlcf(Type.TelephonistType.LambdaCallFunction call) {
            super(call);
        }

        @Override
        public Value call(DefinitiveValEnv<Value> dve) {
            return call.call(dve.arg());
        }
    }

    private static class Nlcf2Lcf extends X2Y<Type.TelephonistType.NativeLambdaCallFunction> implements Type.TelephonistType.LambdaCallFunction {
        private Nlcf2Lcf(Type.TelephonistType.NativeLambdaCallFunction call) {
            super(call);
        }

        @Override
        public Value call(Value arg) {
            try {
                return call.call((Val)arg);
            } catch (NativeExecutionException e) {
                throw e;
            } catch (Exception e) {
                throw new NativeExecutionException(e);
            }
        }
    }

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
