package org.fw.core.state.obj;

import org.fw.base.Val;
import org.fw.core.state.operation.Operation;

public final class ConcreteAtomObj extends AbstractObj implements AtomObj {
    private Val value;

    public ConcreteAtomObj(Val value, Scope owner) {
        super(owner);
        this.value = value;
    }

    public static ConcreteAtomObj of(Val value, Scope owner) {
        return new ConcreteAtomObj(value, owner);
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

    private final Val asVal = Val._NEW_INSTANCE_(LaserPointerFw.laserPointer, this);

    @Override
    public Val asVal() {
        return asVal;
    }
}
