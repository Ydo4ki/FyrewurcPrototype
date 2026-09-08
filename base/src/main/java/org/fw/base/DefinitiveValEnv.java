package org.fw.base;

import org.fw.core.abstrait.Value;

public final class DefinitiveValEnv<V extends Value> {
    private final Val self;
    private final V arg;

    DefinitiveValEnv(Val self, V arg) {
        this.self = self;
        this.arg = arg;
    }

    public Val self() {
        return self;
    }

    public V arg() {
        return arg;
    }

    /* instancer */
    public Val instance(Object payload) {
        return Val.of(self.asType(), payload);
    }

    /* unpacker */
    @SuppressWarnings("unchecked")
    public <T> T unpack(Val val) {
        if (val.getType().equals(self.asType()))
            return (T) val.getValue();

        throw new SecurityException("Attempt to unpack external type: " + self.asType());
    }

    @SuppressWarnings("unchecked")
    public <T extends Value> DefinitiveValEnv<T> recast() {
        return (DefinitiveValEnv<T>) this;
    }
}
