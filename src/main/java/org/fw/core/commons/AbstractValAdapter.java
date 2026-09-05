package org.fw.core.commons;

import org.fw.core.base.Val;

import java.util.Objects;

public abstract class AbstractValAdapter implements ValAdapter {
    private final Val val;

    protected AbstractValAdapter(Val val) {
        this.val = val;
    }

    @Override
    public Val asVal() {
        return val;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AbstractValAdapter that = (AbstractValAdapter) o;
        return Objects.equals(val, that.val);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(val);
    }
}
