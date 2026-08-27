package org.fw.core.state.obj;

import java.util.Objects;

public abstract class AbstractObj implements Obj {
    private final Obj owner;
    private final State state;

    protected AbstractObj(Scope owner) {
        Objects.requireNonNull(owner);
        this.owner = owner;
        this.state = owner.state();
        owner.add(this);
    }

    @Override
    public final State state() {
        return state;
    }

    @Override
    public Obj partOf() {
        return owner;
    }
}
