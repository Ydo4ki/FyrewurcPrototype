package org.fw.core.state.operation;

import org.fw.core.base.Val;
import org.fw.core.state.LaserPointerFw;
import org.fw.core.state.obj.State;
import org.fw.core.state.obj.ValObj;

public final class CreateObjectOperation extends Operation {

    private final Val initialValue;

    public CreateObjectOperation(Val initialValue) {
        this.initialValue = initialValue;
    }

    @Override
    public Val execute(State state) {
        ValObj obj = new ValObj(initialValue, state);
        return Val.of(LaserPointerFw.laserPointer, obj);
    }

    @Override
    protected boolean isPure0() {
        return false;
    }
}
