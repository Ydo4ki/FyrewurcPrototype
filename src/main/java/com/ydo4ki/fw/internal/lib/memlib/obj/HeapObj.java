package com.ydo4ki.fw.internal.lib.memlib.obj;

import com.ydo4ki.fw.internal.lib.memlib.HeapFw;
import org.fw.core.base.Val;
import org.fw.core.state.obj.Scope;
import com.ydo4ki.fw.internal.lib.stdlib.state.SystemOperation;
import org.fw.core.state.obj.AbstractObj;

public final class HeapObj extends AbstractObj {

    public static final HeapObj systemHeap = new HeapObj(SystemOperation.systemState.scope());

    public HeapObj(Scope scope) {
        super(scope);
    }

    private final Val asVal = Val.of(HeapFw.heap, this);

    @Override
    public Val asVal() {
        return asVal;
    }
}

