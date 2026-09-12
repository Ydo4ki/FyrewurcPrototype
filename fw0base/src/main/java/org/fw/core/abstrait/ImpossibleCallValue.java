package org.fw.core.abstrait;

import org.fw.base.Val;
import org.fw.core.state.obj.State;

import java.util.Objects;

public final class ImpossibleCallValue implements Value {

    private final Value a, b;

    public ImpossibleCallValue(Value a, Value b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public Value call(Value arg) {
        return new ImpossibleCallValue(a, b);
    }

    @Override
    public boolean impliesEquality(Val val) {
        return false;
    }

    @Override
    public Value invoke(State state) {
        return asVal().invoke(state);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ImpossibleCallValue that = (ImpossibleCallValue) o;
        return Objects.equals(a, that.a) && Objects.equals(b, that.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }

    @Override
    public String toString() {
        return a + "(" + b + ")";
    }
}
