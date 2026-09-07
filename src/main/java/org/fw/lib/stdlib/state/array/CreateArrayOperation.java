package org.fw.lib.stdlib.state.array;

import org.fw.core.abstrait.Value;
import org.fw.core.base.Val;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;

import java.util.function.IntFunction;

@Deprecated
public final class CreateArrayOperation extends Operation {

    private final int size;
    private final IntFunction<Operation> initialize;

    public CreateArrayOperation(int size, IntFunction<Operation> initialize) {
        this.size = size;
        this.initialize = initialize;
    }

    @Override
    public Value apply(State state) {
        Val[] value = new Val[size];
        for (int i = 0; i < value.length; i++) {
            value[i] = (Val) initialize.apply(i).apply(state);
        }
        ValArrayObj obj = new ValArrayObj(value, state.scope());
        return Val._NEW_INSTANCE_(WidePointerFw.widePointer, obj);
    }
}
