package org.fw.core.state.obj;

import org.fw.base.Val;

public interface AtomObj extends Obj {

    Val read(State state);

    void write(State state, Val x);
}
