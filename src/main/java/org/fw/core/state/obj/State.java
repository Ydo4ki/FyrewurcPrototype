package org.fw.core.state.obj;

import org.fw.core.base.Context;
import org.fw.core.base.Val;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

public final class State implements Obj {
    private final Map<Obj, Obj> objects = new WeakHashMap<>();

    public static State eternal() {
        return new State();
    }

    public static <T> T performAndDie(Function<State, T> function) {
        State state = new State();
        T ret = function.apply(state);
        state.shmert();
        return ret;
    }

    public void shmert() {
        for (Obj obj : objects.values()) {
            obj.shmert();
        }
    }

    void add(Obj obj) {
        objects.put(obj, obj);
    }

    void remove(Obj obj) {
        objects.remove(obj);
    }

    @Override
    public State owner() {
        return this;
    }
}
