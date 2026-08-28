package org.fw.core.state.obj;

import org.fw.core.base.Val;

public interface AtomObj extends Obj {

    static ConcreteAtomObj of(Val value, Scope owner) {
        return new ConcreteAtomObj(value, owner);
    }

    Val read(State state);

    void write(State state, Val x);
}
