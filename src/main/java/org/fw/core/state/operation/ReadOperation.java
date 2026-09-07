package org.fw.core.state.operation;

import org.fw.core.abstrait.Value;
import org.fw.core.state.obj.Obj;
import org.fw.core.state.obj.State;
import org.fw.core.state.obj.AtomObj;

public final class ReadOperation extends Operation {
    private final AtomObj obj;

    ReadOperation(AtomObj obj) {
        this.obj = obj;
    }

    public Obj obj() {
        return obj;
    }

    @Override
    public Value apply(State state) {
        return obj.read(state);
    }
}
