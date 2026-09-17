package org.fw.core.state.operation;

import org.fw.core.abstrait.Value;
import org.fw.base.Val;
import org.fw.core.state.obj.*;

import java.util.Objects;

public final class CreateObjectOperation extends Operation {

    private final Scope scope;
    private final Val initialValue;

    public CreateObjectOperation(Scope scope, Val initialValue) {
        this.scope = scope;
        this.initialValue = initialValue;
    }

    @Override
    public Value apply(State state) {
        if (scope.isInside(state))
            return Operation.unit;
        return scope.create(initialValue).asValHandle();
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
