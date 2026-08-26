package org.fw.core.base;

import org.fw.core.ast.Expr;
import org.fw.lib.stdlib.constraint._Constraint;

import java.util.Objects;
import java.util.function.Supplier;

public final class TelephonistType extends Type {
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
