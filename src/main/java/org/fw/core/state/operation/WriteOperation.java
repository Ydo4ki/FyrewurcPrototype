package org.fw.core.state.operation;

import org.fw.core.base.Context;
import org.fw.core.base.Val;
import org.fw.core.state.obj.Obj;

public final class WriteOperation extends Operation {
    private final Obj.ValObj obj;
    private final Val x;

    WriteOperation(Obj.ValObj obj, Val x) {
        this.obj = obj;
        this.x = x;
    }

    public Val x() {
        return x;
    }

    public Obj obj() {
        return obj;
    }

    @Override
    public Val execute(Context context) {
        obj.write(context, x);
        return Operation.unit;
    }

    @Override
    protected boolean isPure() {
        return false;
    }
}
