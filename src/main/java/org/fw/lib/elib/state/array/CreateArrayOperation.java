package org.fw.lib.elib.state.array;

import org.fw.core.base.Val;
import org.fw.core.state.WidePointer;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;

import java.util.function.IntFunction;

public final class CreateArrayOperation extends Operation {

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
        ValArrayObj obj = new ValArrayObj(value, state);
        return Val.of(WidePointer.widePointer, obj);
    }

    @Override
    protected boolean isPure0() {
        return false;
    }
}
