package org.fw.core.state.obj;

import java.util.Objects;

abstract class AbstractObj implements Obj {
    private final State state;

    protected AbstractObj(State state) {
        Objects.requireNonNull(state);
        this.state = state;
        state.add(this);
    }

    @Override
    public final State state() {
        return state;
    }
}
