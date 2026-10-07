package org.fw.core.state.obj;

import org.fw.base.MagicPowers;
import org.fw.base.Val;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

public final class Scope implements Obj {
    private static final MagicPowers magic = MagicPowers.getMagicPowers();

    private final Obj owner;
    private long nextKey;
    private final Map<Val, Val> objects = new WeakHashMap<>();

    public Scope(Obj owner) {
        this.owner = owner;
    }

    public static <T> T performAndDie(Function<Scope, T> function, Obj owner) {
        Scope state = new Scope(owner);
        T ret = function.apply(state);
        state.shmert();
        return ret;
    }

    public LaserPointerFw.ValObj create(Val value) {
        // we'll just use this asVal as a key type for now
        Val key = magic.newInstance(this.asVal.asType(), nextKey++);
        objects.put(key, value);
        return new LaserPointerFw.ValObj(this, key);
    }

    public void set(Val key, Val value) {
        objects.put(key, value);
    }

    public Val get(Val key) {
        return objects.get(key);
    }

    @Override
    public Obj parent() {
        return owner;
    }

    private final Val asVal = magic.newInstance(ScopeFw.scopePointer, this);

    @Override
    public Val asValHandle() {
        return asVal;
    }

    @Override
    public void shmert() {
//        for (Obj obj : objects.values()) {
//            obj.shmert();
//        }
    }
}
