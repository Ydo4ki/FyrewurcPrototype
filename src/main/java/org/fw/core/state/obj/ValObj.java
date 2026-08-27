package org.fw.core.state.obj;

import org.fw.core.base.Val;

public interface ValObj extends Obj {

    Val read(State state);

    void write(State state, Val x);
}
