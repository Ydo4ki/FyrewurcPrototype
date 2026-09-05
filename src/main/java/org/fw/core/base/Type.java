package org.fw.core.base;

import org.fw.core.commons.ValAdapter;
import org.fw.core.base.contract.CallContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Type implements ValAdapter {

    Type() {}

    abstract Val callInstance(Val instance, Val arg);

    abstract CallContract instanceContract(Val instance);

    public abstract Val asVal();

    static final class ValType extends Type {

        private final Val asVal;

        public ValType(Val asVal) {
            this.asVal = asVal;
        }

        @Override
        public Val callInstance(Val instance, Val arg) {
            return asVal.call(CallFw.fwCall(instance, arg));
        }

        @Override
        CallContract instanceContract(Val instance) {
            return CallContract.unknown();
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
        Val callInstance(Val instance, Val arg) {
            try {
                Val v = instance._unpack(Telephonist.class).function().call(arg);
                if (v == null)
                    return Unspecified.unspecified(instance, arg);
                return v;
            } catch (Exception e) {
                System.out.println("UNEXPECTED EXCEPTION, AAAAA:");
                e.printStackTrace(System.out);
                return Unspecified.unspecified(instance, arg);
            }
        }

        @Override
        CallContract instanceContract(Val instance) {
            return instance._unpack(Telephonist.class).contract();
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

        public interface CallFunction {
            Val call(Val arg) throws Exception;
        }

        public interface ConstraintCallFunction {
            Constraint call(Constraint arg) throws Exception;
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
            private final CallFunction function;
            private final CallContract contract;

            public Telephonist(CallFunction function, CallContract contract) {
                this.function = function;
                this.contract = contract;
            }

            public CallFunction function() {
                return function;
            }

            public CallContract contract() {
                return contract;
            }

            @Override
            public boolean equals(Object o) {
                if (o == null || getClass() != o.getClass()) return false;
                Telephonist that = (Telephonist) o;
                return Objects.equals(function, that.function) && Objects.equals(contract, that.contract);
            }

            @Override
            public int hashCode() {
                return Objects.hash(function, contract);
            }
        }
    }
}