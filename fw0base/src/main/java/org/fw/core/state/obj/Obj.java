package org.fw.core.state.obj;

import org.fw.base.Val;

public interface Obj extends State {

    Obj parent();

    Val asValHandle();

    default void shmert() {

    }

    default boolean isInside(State state) {
        State p = this;
        while (p != null) {
            if (p == state)
                return true;

            p = p.parent();
        }
        return false;
    }
}
