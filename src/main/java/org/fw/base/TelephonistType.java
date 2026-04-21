package org.fw.base;

import org.fw.ast.Expr;

import java.util.Objects;
import java.util.function.Supplier;

public final class TelephonistType implements Type {
    private final Val val;

    TelephonistType(Val.TelephonistVal val) {
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

    public record Telephonist(Supplier<Expr> representation, CallFunction function) {
    }
}
