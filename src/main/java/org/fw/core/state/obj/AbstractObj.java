package org.fw.core.state.obj;

import java.util.Objects;

abstract class AbstractObj implements Obj {
    private State owner;

    protected AbstractObj(State owner) {
        Objects.requireNonNull(owner);
        this.owner = owner;
        owner.add(this);
    }

    @Override
    public final State owner() {
        return owner;
    }
}
