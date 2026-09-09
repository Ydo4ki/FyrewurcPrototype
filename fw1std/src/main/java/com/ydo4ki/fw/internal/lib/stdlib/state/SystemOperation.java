package com.ydo4ki.fw.internal.lib.stdlib.state;

import org.fw.core.abstrait.Value;
import org.fw.base.Val;
import org.fw.core.state.operation.Operation;
import org.fw.core.state.obj.State;

public abstract class SystemOperation extends Operation {

    public static final State systemState = State.eternal();

    public SystemOperation() {}

    @Override
    public final Value apply(State state) {
        // errr ok I'm not sure how to determine if that's a system context or not
        // and it's not like it will be much useful later
        // I should probably create a random instance and call it a system context
        if (state != systemState) {
            return Operation.unit;
        }
        return apply0();
    }

    protected abstract Val apply0();

}
