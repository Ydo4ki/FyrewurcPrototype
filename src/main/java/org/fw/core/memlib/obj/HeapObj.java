package org.fw.core.memlib.obj;

import org.fw.core.lib.state.SystemOperation;
import org.fw.core.state.obj.AbstractObj;
import org.fw.core.state.obj.State;

public final class HeapObj extends AbstractObj {

    public static final HeapObj systemHeap = new HeapObj(SystemOperation.systemState);

    public HeapObj(State state) {
        super(state);
    }
}

