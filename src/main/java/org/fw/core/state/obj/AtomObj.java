package org.fw.core.state.obj;

import org.fw.core.base.Val;
import org.fw.core.state.operation.Operation;

public final class AtomObj extends AbstractObj implements ValObj {
    private Val value;

    public AtomObj(Val value, Scope owner) {
        super(owner);
        this.value = value;
    }

    public static AtomObj of(Val value, Scope owner) {
        return new AtomObj(value, owner);
    }

    public Val read(State state) {
        if (state() != state)
            return Operation.unit; // c'mon at least use exceptions you're getting too far with this
        return value;
    }

    public void write(State state, Val x) {
        if (state() != state)
            return;
        value = x;
    }
}
