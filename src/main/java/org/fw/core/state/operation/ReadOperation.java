package org.fw.core.state.operation;

import org.fw.core.base.Context;
import org.fw.core.base.Val;
import org.fw.core.state.obj.Obj;

public final class ReadOperation extends Operation {
    private final Obj.ValObj obj;

    ReadOperation(Obj.ValObj obj) {
        this.obj = obj;
    }

    public Obj obj() {
        return obj;
    }

    @Override
    public Val execute(Context context) {
        return obj.read(context);
    }
}
