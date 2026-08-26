package org.fw.core.contract;

import org.fw.core.base.Val;

import java.util.Objects;
import java.util.function.Function;

public final class CallContract extends Contract {

    public static final CallContract UNKNOWN = c(c -> _Constraint.free);

    public static CallContract c(Function<_Constraint, _Constraint> function) {
        return new CallContract(function, null);
    }

    public static CallContract unknown() {
        return UNKNOWN;
    }

    public static CallContract constant(Val val) {
        final _Constraint c0 = _Constraint.equals(val);
        return new CallContract(c -> c0, val);
    }

    private final Function<_Constraint, _Constraint> m_resultConstraint;
    private final Val concreteValueIfImplied;

    CallContract(Function<_Constraint, _Constraint> resultConstraint, Val concreteValueIfImplied) {
        this.m_resultConstraint = resultConstraint;
        this.concreteValueIfImplied = concreteValueIfImplied;
    }

    public boolean isConst() {
        return concreteValueIfImplied != null;
    }

    public _Constraint resultConstraint(_Constraint constraint) {
        return m_resultConstraint.apply(constraint);
    }

    public _Constraint constraintOfResult() {
        return resultConstraint(_Constraint.free);
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
        return concreteValueIfImplied;
    }
}
