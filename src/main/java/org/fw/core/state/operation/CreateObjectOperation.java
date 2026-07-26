package org.fw.core.state.operation;

import org.fw.core.base.Context;
import org.fw.core.base.Val;
import org.fw.core.lib.state.LaserPointerFw;
import org.fw.core.state.obj.Obj;

public class CreateObjectOperation extends Operation {

    private final Val initialValue;

    public CreateObjectOperation(Val initialValue) {
        this.initialValue = initialValue;
    }

    @Override
    public Val execute(Context context) {
        Obj.ValObj obj = new Obj.ValObj(initialValue, context.state());
        return Val.of(LaserPointerFw.laserPointer, obj);
    }
}
