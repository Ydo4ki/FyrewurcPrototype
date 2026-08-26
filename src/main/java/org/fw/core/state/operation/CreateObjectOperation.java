package org.fw.core.state.operation;

import org.fw.core.base.Val;
import org.fw.core.contract.InvokeContract;
import org.fw.core.contract._Constraint;
import org.fw.core.state.LaserPointerFw;
import org.fw.core.state.obj.State;
import org.fw.core.state.obj.ValObj;

// todo: return scopes and add it to them
public final class CreateObjectOperation extends Operation {

    private final Val initialValue;

    public CreateObjectOperation(Val initialValue) {
        this.initialValue = initialValue;
    }

    @Override
    public Val apply(State state) {
        ValObj obj = ValObj.of(initialValue, state);
        return Val.of(LaserPointerFw.laserPointer, obj);
    }

    @Override
    public InvokeContract contract() {
        return InvokeContract.returnsBrandNew(_Constraint.of(LaserPointerFw.laserPointer), true, false);
    }
}
