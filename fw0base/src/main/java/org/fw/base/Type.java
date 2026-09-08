package org.fw.base;

import org.fw.core.abstrait.Value;
import org.fw.core.commons.ValAdapter;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class Type implements ValAdapter {

    Type() { }

    abstract Value callInstance(Val instance, Value arg);

    abstract Value invokeInstance(Val val, State state);

    public abstract Val asVal();

    public Type getPayloadType() {
        return null;
    }

    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "OptionalAssignedToNull"})
    static final class ValType extends Type {

        private final Val asVal;
        private Optional<Type> payloadType = null;

        public Type getPayloadType() {
            if (payloadType == null) {
                Val ret = (Val) this.asVal().get("Payload");
                payloadType = Optional.ofNullable(TypePayloadInfo.value(ret));
            }
            return payloadType.orElse(null);
        }

        ValType(Val asVal) {
            this.asVal = asVal;
        }

        @Override
        public Value callInstance(Val instance, Value arg) {
            if (arg instanceof Val)
                return asVal.call(CallFw.fwCall(instance, (Val) arg));
            return asVal.call(CallFw.call_t.get("construct").call(instance).call(arg));
        }

        @Override
        Value invokeInstance(Val val, State state) {
            if (val.getValue() instanceof Operation) {
                Operation op = (Operation)val.getValue();
                return op.apply(state);
            }
            return null;
        }

        @Override
        public Val asVal() {
            return asVal;
        }

        @Override
        public String toString() {
            return asVal.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ValType)) return false;
            ValType that = (ValType) o;
            return java.util.Objects.equals(asVal, that.asVal);
        }

        @Override
        public int hashCode() {
            return asVal.hashCode();
        }
    }

    public static final class TelephonistType extends Type {
        private final int depth;
        private Val asVal;

        TelephonistType(int depth) {
            super();
            this.depth = depth;
        }

        @Override
        Val callInstance(Val instance, Value arg) {
            Value v = ((Telephonist)instance.getValue()).call(instance, arg);
            if (!(v instanceof Val))
                return Unspecified.unspecified(instance, arg);

            return (Val) v;
//            try {
//                Value v = instance._unpack(Telephonist.class).function().call(arg);
//                if (!(v instanceof Val))
//                    return Unspecified.unspecified(instance, arg);
//                return (Val) v;
//            } catch (Exception e) {
//                System.out.println("UNEXPECTED EXCEPTION, AAAAA:");
//                e.printStackTrace(System.out);
//                return Unspecified.unspecified(instance, arg);
//            }
        }

        @Override
        Value invokeInstance(Val instance, State state) {
            return ((Telephonist)instance.getValue()).invoke(state);
        }

        @Override
        public Val asVal() {
            if (asVal == null) {
                this.asVal = Val.telephonistVal(this);
            }
            return asVal;
        }

        @Override
        public String toString() {
            return "Telephonist" + (depth == 0 ? "" : depth);
        }

        public int getDepth() {
            return depth;
        }

        public interface NativeCallFunction {
            Value call(DefinitiveValEnv<Val> arg) throws Exception;
        }

        public interface NativeLambdaCallFunction {
            Value call(Val arg) throws Exception;
        }

        public interface CallFunction {
            Value call(DefinitiveValEnv<Value> dve);
        }

        public interface LambdaCallFunction {
            Value call(Value arg);
        }

        public interface InvokeFunction {
            Value invoke(State state);
        }

        private static final List<TelephonistType> preTelephonists = new ArrayList<>();

        public static TelephonistType of(int depth) {
            int cs;
            while ((cs = preTelephonists.size()) <= depth) {
                preTelephonists.add(new TelephonistType(cs));
            }
            return preTelephonists.get(depth);
        }

        public static final class Telephonist {
            private final String marker;
            private final CallFunction function;
            private final InvokeFunction operation;

            public Telephonist(String marker, CallFunction function, InvokeFunction operation) {
                this.marker = marker;
                this.function = function;
                this.operation = operation;
            }

            @Override
            public boolean equals(Object o) {
                if (o == null || getClass() != o.getClass()) return false;
                Telephonist that = (Telephonist) o;
                return Objects.equals(function, that.function);
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(function);
            }

            @Override
            public String toString() {
                return marker;
            }

            Value call(Val self, Value arg) {
                return function.call(new DefinitiveValEnv<>(self, arg));
            }

            Value invoke(State state) {
                return operation.invoke(state);
            }
        }
    }
}