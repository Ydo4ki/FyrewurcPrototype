package org.fw.core.state.obj;

import org.fw.core.base.Val;
import org.fw.core.state.operation.Operation;

public interface ValObj extends Obj {

    static ValObj of(Val value, State owner) {
        return new ConcreteValObj(value, owner);
    }

    Val read(State state);

    void write(State state, Val x);
}
class ConcreteValObj extends AbstractObj implements ValObj {
    private Val value;
    public ConcreteValObj(Val value, State owner) {
        super(owner);
        this.value = value;
    }

    public Val read(State state) {
        if (state() != state)
            return Operation.unit; // c'mon at least use exceptions you're getting too far with this
        return value;
    }

    public void write(State state, Val x) {
        if (state() != state)
            return;
        value = x;
    }
}