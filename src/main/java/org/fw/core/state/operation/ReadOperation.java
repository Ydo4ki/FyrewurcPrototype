package org.fw.core.state.operation;

import org.fw.core.base.Val;
import org.fw.core.contract.InvokeContract;
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
    public Val apply(State state) {
        return obj.read(state);
    }

    @Override
    public InvokeContract contract() {
        return InvokeContract.readsOnly(obj);
    }
}
