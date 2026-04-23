package org.fw.core.state.obj;

import org.fw.core.base.Context;
import org.fw.core.base.Val;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

public final class Scope implements Obj {
    private final Scope parent;
    private final Map<Obj, Obj> objects = new WeakHashMap<>();

    private Scope(Scope parent) {
        this.parent = parent;
    }

    public static Scope eternal() {
        return new Scope(null);
    }

    public static <T> T performAndDie(Scope parent, Function<Scope, T> function) {
        Scope scope = new Scope(parent);
        T ret = function.apply(scope);
        scope.shmert();
        return ret;
    }

    public Scope parent() {
        return parent;
    }

    public void shmert() {
        for (Obj obj : objects.values()) {
            obj.move(parent);
        }
    }

    void add(Obj obj) {
        objects.put(obj, obj);
    }

    void remove(Obj obj) {
        objects.remove(obj);
    }

    @Override
    public Val read(Context context) {
        // I'll use it later somehow (very soon actually)
        return Val.unspecified;
    }

    @Override
    public void write(Context context, Val x) {
        // I'll use it later somehow
    }

    @Override
    public Scope owner() {
        return this;
    }

    @Override
    public void move(Scope newScope) {
        // uhhh idk
        throw new UnsupportedOperationException("How did you get here");
    }
}
