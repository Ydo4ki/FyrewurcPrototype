package org.fw.core.contract;

import org.fw.core.base.Val;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public final class CallContract extends Contract {

    public static final CallContract UNKNOWN = c(c -> Constraint.free);

    public static CallContract c(Function<Constraint, Constraint> function) {
        return new CallContract(function, null);
    }

    public static CallContract unknown() {
        return UNKNOWN;
    }

    public static CallContract constant(Supplier<Val> val) {
        return new CallContract(c -> Constraint.equals(val.get()), val);
    }

    public static CallContract constant(Val val) {
        final Constraint c0 = Constraint.equals(val);
        return new CallContract(c -> c0, () -> val);
    }

    private final Function<Constraint, Constraint> m_resultConstraint;
    private final Supplier<Val> concreteValueIfImplied;

    CallContract(Function<Constraint, Constraint> resultConstraint, Supplier<Val> concreteValueIfImplied) {
        this.m_resultConstraint = resultConstraint;
        this.concreteValueIfImplied = concreteValueIfImplied;
    }

    public boolean isConst() {
        return concreteValueIfImplied != null;
    }

    public Constraint resultConstraint(Constraint constraint) {
        return m_resultConstraint.apply(constraint);
    }

    public Constraint constraintOfResult() {
        return resultConstraint(Constraint.free);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CallContract that = (CallContract) o;
        return Objects.equals(m_resultConstraint, that.m_resultConstraint);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(m_resultConstraint);
    }

    public Val concreteValueIfImplied() {
        return concreteValueIfImplied.get();
    }
}
