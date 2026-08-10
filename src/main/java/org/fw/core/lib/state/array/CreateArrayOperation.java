package org.fw.core.lib.state.array;

import org.fw.core.base.Val;
import org.fw.core.state.LaserPointerFw;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;

import java.util.function.IntFunction;

public class CreateArrayOperation extends Operation {

    private final int size;
    private final IntFunction<Operation> initialize;

    public CreateArrayOperation(int size, IntFunction<Operation> initialize) {
        this.size = size;
        this.initialize = initialize;
    }

    @Override
    public Val apply(State state) {
        Val[] value = new Val[size];
        for (int i = 0; i < value.length; i++) {
            value[i] = initialize.apply(i).apply(state);
        }
        ArrayObj obj = new ArrayObj(value, state);
        return Val.of(LaserPointerFw.laserPointer, obj);
    }

    @Override
    protected boolean isPure0() {
        return false;
    }
}
