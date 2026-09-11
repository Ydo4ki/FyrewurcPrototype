package org.fw.core.abstrait;

import org.fw.base.Val;
import org.fw.core.constraint.Constraint;

public final class ConstraintValue implements Value {
    private final Constraint constraint;

    public ConstraintValue(Constraint constraint) {
        this.constraint = constraint;
    }

    @Override
    public Value call(Value arg) {
        return new LazyCallValue(this, arg);
    }

    @Override
    public boolean impliesEquality(Val val) {
        return constraint.implies(Constraint.equals(val));
    }
}
