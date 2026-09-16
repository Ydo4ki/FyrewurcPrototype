package org.fw.core.state.obj;

import org.fw.base.Val;
import org.fw.core.state.operation.Operation;

import java.util.Objects;

public final class ValObj implements AtomObj {
    private final Obj owner;
    private Val value;

    public ValObj(Val value, Scope owner) {
        Objects.requireNonNull(owner);
        this.owner = owner;
        owner.add(this);
        this.value = value;
    }

    @Override
    public Obj parent() {
        return owner;
    }

    public static ValObj of(Val value, Scope owner) {
        return new ValObj(value, owner);
    }

    public Val read(State state) {
        if (this.isInside(state))
            return Operation.unit; // c'mon at least use exceptions you're getting too far with this
        return value;
    }

    public void write(State state, Val x) {
        if (this.isInside(state))
            return;
        value = x;
    }

    private final Val asVal = Val._NEW_INSTANCE_(LaserPointerFw.laserPointer, this);

    @Override
    public Val asValHandle() {
        return asVal;
    }
}
