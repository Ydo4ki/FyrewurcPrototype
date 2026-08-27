package org.fw.core.state.obj;

import org.fw.core.base.Val;
import org.fw.core.state.LaserPointerFw;

import java.util.function.Function;

public final class State implements Obj {
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

    private final Val asVal = Val.of(LaserPointerFw.laserPointer, this);

    public Val asVal() {
        return asVal;
    }
}
