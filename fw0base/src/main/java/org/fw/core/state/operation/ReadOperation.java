package org.fw.core.state.operation;

import org.fw.core.abstrait.Value;
import org.fw.core.state.obj.LaserPointerFw;
import org.fw.core.state.obj.Obj;
import org.fw.core.state.obj.State;

public final class ReadOperation extends Operation {
    private final LaserPointerFw.ValObj obj;

    ReadOperation(LaserPointerFw.ValObj obj) {
        this.obj = obj;
    }

    @Override
    public Value apply(State state) {
        return obj.read(state);
    }
}
