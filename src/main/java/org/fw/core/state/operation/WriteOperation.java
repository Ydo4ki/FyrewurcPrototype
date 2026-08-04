package org.fw.core.state.operation;

import org.fw.core.base.Val;
import org.fw.core.state.obj.Obj;
import org.fw.core.state.obj.State;
import org.fw.core.state.obj.ValObj;

public final class WriteOperation extends Operation {
    private final ValObj obj;
    private final Val x;

    WriteOperation(ValObj obj, Val x) {
        this.obj = obj;
        this.x = x;
    }

    public Val x() {
        return x;
    }

    public Obj obj() {
        return obj;
    }

    @Override
    public Val execute(State state) {
        obj.write(state, x);
        return Operation.unit;
    }

    @Override
    protected boolean isPure0() {
        return false;
    }
}
