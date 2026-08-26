package org.fw.core.base;

import org.fw.core.ast.Expr;
import org.fw.core.commons.ValAdapter;
import org.fw.lib.stdlib.constraint._Constraint;

import java.util.Objects;
import java.util.function.Supplier;

public abstract class Type implements ValAdapter {

    Type() {}

    public static Type of(Val.Box val) {
        return new ValType(val);
    }

    abstract Val callInstance(Val instance, Val arg);

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
            _Constraint call(_Constraint arg) throws Exception;
        }

        public static final class Telephonist {
            private final Supplier<Expr> representation;
            private final CallFunction function;

            public Telephonist(Supplier<Expr> representation, CallFunction function) {
                this.representation = representation;
                this.function = function;
            }

            public Supplier<Expr> representation() {
                return representation;
            }

            public CallFunction function() {
                return function;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == this) return true;
                if (obj == null || obj.getClass() != this.getClass()) return false;
                Telephonist that = (Telephonist) obj;
                return Objects.equals(this.representation, that.representation) &&
                        Objects.equals(this.function, that.function);
            }

            @Override
            public int hashCode() {
                return Objects.hash(representation, function);
            }

            @Override
            public String toString() {
                return "Telephonist[" +
                        "representation=" + representation + ", " +
                        "function=" + function + ']';
            }
        }
    }
}