package org.fw.core.state.obj;

import java.util.Objects;

abstract class AbstractObj implements Obj {
    private Scope owner;

    protected AbstractObj(Scope owner) {
        Objects.requireNonNull(owner);
        this.owner = owner;
        owner.add(this);
    }

    @Override
    public final Scope owner() {
        return owner;
    }

    @Override
    public final void move(Scope newScope) {
        owner.remove(this);
        owner = newScope;
        owner.add(this);
    }
}
