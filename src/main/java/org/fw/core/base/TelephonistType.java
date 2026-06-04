package org.fw.core.base;

import org.fw.core.ast.Expr;

import java.util.Objects;
import java.util.function.Supplier;

public final class TelephonistType extends Type {
    private final Val val;

    TelephonistType(Val.TelephonistVal val) {
        super();
        this.val = val;
    }

    @Override
    public Val callInstance(Val instance, Val arg, Context context) {
        Val ret = instance._unpack(Telephonist.class).function().call(arg, context);
        if (ret == null)
            throw new NullPointerException("INVALID RESULT OF EXTERN T: " + instance + "(" + arg + ") -> null");
        return ret;
    }

    @Override
    public Val asVal() {
        return val;
    }

//    @Override
//    public Expr instanceToExpr(Val instance, Context context) {
//        return instance._unpack(Telephonist.class).representation.get();
//    }

    @Override
    public String toString() {
        return val.toString();
    }

    public interface CallFunction {
        Val call(Val arg, Context context);
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
