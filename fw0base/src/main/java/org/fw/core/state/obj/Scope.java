package org.fw.core.state.obj;

import org.fw.base.Val;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

public final class Scope implements Obj {
    private final Obj owner;
    private final Map<Obj, Obj> objects = new WeakHashMap<>();

    public Scope(Obj owner) {
        this.owner = owner;
    }

    public static <T> T performAndDie(Function<Scope, T> function, Obj owner) {
        Scope state = new Scope(owner);
        T ret = function.apply(state);
        state.shmert();
        return ret;
    }

    void add(Obj obj) {
        objects.put(obj, obj);
    }

    @Override
    public Obj parent() {
        return owner;
    }

    private final Val asVal = Val._NEW_INSTANCE_(ScopeFw.scopePointer, this);

    @Override
    public Val asValHandle() {
        return asVal;
    }

    @Override
    public void shmert() {
        for (Obj obj : objects.values()) {
            obj.shmert();
        }
    }
}
