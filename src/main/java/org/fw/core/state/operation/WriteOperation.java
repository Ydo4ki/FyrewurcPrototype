package org.fw.core.state.operation;

import org.fw.core.base.Val;
import org.fw.core.contract.InvokeContract;
import org.fw.core.contract._Constraint;
import org.fw.core.state.obj.Obj;
import org.fw.core.state.obj.State;
import org.fw.core.state.obj.AtomObj;

public final class WriteOperation extends Operation {
    private final AtomObj obj;
    private final Val x;

    WriteOperation(AtomObj obj, Val x) {
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
    public Val apply(State state) {
        obj.write(state, x);
        return Operation.unit;
    }

    @Override
    public InvokeContract contract() {
        return InvokeContract.writesOnly(_Constraint.equals(Operation.unit), obj);
    }
}
