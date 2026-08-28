package org.fw.core.state.obj;

import org.fw.core.base.Val;
import org.fw.lib.stdlib.state.ScopeFw;

import java.util.Map;
import java.util.WeakHashMap;

public final class Scope implements Obj {
    private final Obj owner;
    private final Map<Obj, Obj> objects = new WeakHashMap<>();

    Scope(Obj owner) {
        this.owner = owner;
    }

    void add(Obj obj) {
        objects.put(obj, obj);
    }

    @Override
    public State state() {
        return owner.state();
    }

    @Override
    public Obj partOf() {
        return owner;
    }

    private final Val asVal = Val.of(ScopeFw.scopePointer, this);

    @Override
    public Val asVal() {
        return asVal;
    }

    @Override
    public void shmert() {
        for (Obj obj : objects.values()) {
            obj.shmert();
        }
    }
}
