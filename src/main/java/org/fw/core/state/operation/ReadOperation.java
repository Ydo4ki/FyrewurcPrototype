package org.fw.core.state.operation;

import org.fw.core.base.Val;
import org.fw.core.state.obj.Obj;
import org.fw.core.state.obj.State;
import org.fw.core.state.obj.ValObj;

public final class ReadOperation extends Operation {
    private final ValObj obj;

    ReadOperation(ValObj obj) {
        this.obj = obj;
    }

    public Obj obj() {
        return obj;
    }

    @Override
    public Val apply(State state) {
        return obj.read(state);
    }

    @Override
    protected boolean isPure0() {
        return false;
    }
}
