package org.fw.core.base;

import org.fw.core.commons.ValAdapter;
import org.fw.core.contract.CallContract;
import org.fw.core.contract.Constraint;

import java.util.Objects;

public abstract class Type implements ValAdapter {

    Type() {}

    public static Type of(Val.Box val) {
        return new ValType(val);
    }

    abstract Val callInstance(Val instance, Val arg);

    abstract CallContract instanceContract(Val instance);

    public abstract Val asVal();

    public static final class ValType extends Type {

        private final Val.Box asVal;

        public ValType(Val.Box asVal) {
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
            return java.util.Objects.hash(asVal);
        }
    }

    public static final class TelephonistType extends Type {
        private final Val val;

        TelephonistType(Val.TelephonistVal val) {
            super();
            this.val = val;
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
            return val;
        }

        @Override
        public String toString() {
            return val.toString();
        }

        public interface CallFunction {
            Val call(Val arg) throws Exception;
        }

        public interface ConstraintCallFunction {
            Constraint call(Constraint arg) throws Exception;
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