package org.fw.core.state.operation;

import org.fw.core.base.Val;
import org.fw.core.state.obj.Obj;
import org.fw.core.state.obj.State;

public final class CreateObjectOperation extends Operation {

    private final Val initialValue;

    public CreateObjectOperation(Val initialValue) {
        this.initialValue = initialValue;
    }

    @Override
    public Val execute(State state) {
        Obj.ValObj obj = new Obj.ValObj(initialValue, state);
        return Val.of(LaserPointerFw.laserPointer, obj);
    }

    @Override
    protected boolean isPure0() {
        return false;
    }
}
