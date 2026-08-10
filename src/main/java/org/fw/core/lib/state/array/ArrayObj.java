package org.fw.core.lib.state.array;

import org.fw.core.base.Val;
import org.fw.core.state.obj.AbstractObj;
import org.fw.core.state.obj.State;
import org.fw.core.state.obj.ValObj;
import org.fw.core.state.operation.Operation;

public final class ArrayObj extends AbstractObj {
    private final Val[] value;

    public ArrayObj(Val[] value, State owner) {
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

    private static class ReferenceValObj implements ValObj {
        private final ArrayObj arrayObj;
        private final int index;

        ReferenceValObj(ArrayObj arrayObj, int index) {
            this.arrayObj = arrayObj;
            this.index = index;
        }

        @Override
        public Val read(State state) {
            return arrayObj.read(state, index);
        }

        @Override
        public void write(State state, Val x) {
            arrayObj.write(state, index, x);
        }

        @Override
        public State state() {
            return arrayObj.state();
        }
    }
}
