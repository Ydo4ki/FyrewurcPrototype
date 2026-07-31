package org.fw.core.state.operation;

import org.fw.core.base.Val;
import org.fw.core.state.obj.Obj;
import org.fw.core.state.obj.State;

public final class ReadOperation extends Operation {
    private final Obj.ValObj obj;

    ReadOperation(Obj.ValObj obj) {
        this.obj = obj;
    }

    public Obj obj() {
        return obj;
    }

    @Override
    public Val execute(State state) {
        return obj.read(state);
    }

    @Override
    protected boolean isPure0() {
        return false;
    }
}
