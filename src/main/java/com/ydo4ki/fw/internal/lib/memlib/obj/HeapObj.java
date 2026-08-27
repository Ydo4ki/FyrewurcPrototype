package com.ydo4ki.fw.internal.lib.memlib.obj;

import org.fw.core.state.obj.Scope;
import org.fw.lib.stdlib.state.SystemOperation;
import org.fw.core.state.obj.AbstractObj;

public final class HeapObj extends AbstractObj {

    public static final HeapObj systemHeap = new HeapObj(SystemOperation.systemState.scope());

    public HeapObj(Scope scope) {
        super(scope);
    }
}

