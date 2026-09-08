package org.fw.core.state.obj;

import org.fw.base.Val;

import java.util.function.Function;

// todo: make it so any object can be state
//  otherwise we can't move atom obj to std
// but wait how do we ensure that state allows objects creation when its needed
public final class State implements Obj {
    // questionable, maybe I should just allow states to be inside other states but keep this as optional
    private final Scope scope = new Scope(this);

    public static State eternal() {
        return new State();
    }

    private State() { }

    public static <T> T performAndDie(Function<State, T> function) {
        State state = new State();
        T ret = function.apply(state);
        state.shmert();
        return ret;
    }

    public Scope scope() {
        return scope;
    }

    public void shmert() {
        scope.shmert();
    }

    @Override
    public State state() {
        return this;
    }

    @Override
    public Obj partOf() {
        return null;
    }

    private final Val asVal = Val._NEW_INSTANCE_(StatePointerFw.statePointer, this);

    @Override
    public Val asVal() {
        return asVal;
    }
}
