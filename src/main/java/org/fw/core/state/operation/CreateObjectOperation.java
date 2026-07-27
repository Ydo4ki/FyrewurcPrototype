package org.fw.core.state.operation;

import org.fw.core.base.context.Context;
import org.fw.core.base.Val;
import org.fw.core.state.obj.Obj;

public final class CreateObjectOperation extends Operation {

    private final Val initialValue;

    public CreateObjectOperation(Val initialValue) {
        this.initialValue = initialValue;
    }

    @Override
    public Val execute(Context context) {
        Obj.ValObj obj = new Obj.ValObj(initialValue, context.state());
        return Val.of(LaserPointerFw.laserPointer, obj);
    }

    @Override
    protected boolean isPure0() {
        return false;
    }
}
