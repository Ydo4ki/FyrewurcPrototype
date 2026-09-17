package org.fw.core.state.operation;

import org.fw.core.abstrait.Value;
import org.fw.base.Val;
import org.fw.core.state.obj.LaserPointerFw;
import org.fw.core.state.obj.Obj;
import org.fw.core.state.obj.State;

public final class WriteOperation extends Operation {
    private final LaserPointerFw.ValObj obj;
    private final Val x;

    WriteOperation(LaserPointerFw.ValObj obj, Val x) {
        this.obj = obj;
        this.x = x;
    }

    @Override
    public Value apply(State state) {
        obj.write(state, x);
        return Operation.unit;
    }
}
