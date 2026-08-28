package org.fw.core.state.operation;

import org.fw.core.base.Val;
import org.fw.core.contract.InvokeContract;
import org.fw.core.contract.Constraint;
import org.fw.core.state.obj.AtomObj;
import org.fw.core.state.obj.Scope;
import org.fw.core.state.obj.State;
import org.fw.lib.stdlib.state.LaserPointerFw;

import java.util.Objects;

public final class CreateObjectOperation extends Operation {

    private final Scope scope;
    private final Val initialValue;

    public CreateObjectOperation(Scope scope, Val initialValue) {
        this.scope = scope;
        this.initialValue = initialValue;
    }

    @Override
    public Val apply(State state) {
        if (state != scope.state()) 
            return Operation.unit;
        AtomObj obj = AtomObj.of(initialValue, scope);
        return Val.of(LaserPointerFw.laserPointer, obj);
    }

    @Override
    public InvokeContract contract() {
        return InvokeContract.returnsBrandNew(Constraint.type(LaserPointerFw.laserPointer), true, false);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CreateObjectOperation that = (CreateObjectOperation) o;
        return Objects.equals(scope, that.scope) && Objects.equals(initialValue, that.initialValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scope, initialValue);
    }
}
