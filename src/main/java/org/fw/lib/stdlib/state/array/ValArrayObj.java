package org.fw.lib.stdlib.state.array;

import org.fw.core.base.Val;
import org.fw.core.state.obj.*;
import org.fw.core.state.operation.Operation;

public final class ValArrayObj extends AbstractObj {
    private final Val[] value;

    public ValArrayObj(Val[] value, Scope owner) {
        super(owner);
        this.value = value;
    }

    public ValObj getRef(int index) {
        return new ReferenceValObj(this, index);
    }

    public Val read(State state, int index) {
        if (state() != state)
            return Operation.unit; // c'mon at least use exceptions you're getting too far with this
        return value[index];
    }

    public void write(State state, int index, Val x) {
        if (state() != state)
            return;
        value[index] = x;
    }

    public int size() {
        return value.length;
    }

    private static class ReferenceValObj implements ValObj {
        private final ValArrayObj valArrayObj;
        private final int index;

        ReferenceValObj(ValArrayObj valArrayObj, int index) {
            this.valArrayObj = valArrayObj;
            this.index = index;
        }

        @Override
        public Val read(State state) {
            return valArrayObj.read(state, index);
        }

        @Override
        public void write(State state, Val x) {
            valArrayObj.write(state, index, x);
        }

        @Override
        public State state() {
            return valArrayObj.state();
        }

        @Override
        public Obj partOf() {
            return valArrayObj;
        }
    }
}
