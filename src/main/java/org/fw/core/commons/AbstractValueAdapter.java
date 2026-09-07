package org.fw.core.commons;

import org.fw.core.abstrait.Value;
import org.fw.core.base.Val;

import java.util.Objects;

public abstract class AbstractValueAdapter implements ValueAdapter {
    private final Value val;

    protected AbstractValueAdapter(Value val) {
        this.val = val;
    }

    @Override
    public Value asValue() {
        return val;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AbstractValueAdapter that = (AbstractValueAdapter) o;
        return Objects.equals(val, that.val);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(val);
    }
}
