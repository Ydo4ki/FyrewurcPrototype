package org.fw.base;

import org.fw.core.abstrait.Value;

import java.util.Objects;

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

        throw new SecurityException("Attempt to unpack external type: " + self.asType() + " while this unpacker is for " + self.asType());
    }

    public <T> T unpack(Val val, Class<T> cls) {
        return unpack(val);
    }

    @SuppressWarnings("unchecked")
    public DefinitiveValEnv<Val> recastAsVal() {
        return new DefinitiveValEnv<>(self, arg.asVal());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DefinitiveValEnv<?> that = (DefinitiveValEnv<?>) o;
        return Objects.equals(self, that.self) && Objects.equals(arg, that.arg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(self, arg);
    }

    @Override
    public String toString() {
        return "DefinitiveValEnv{" +
                "self=" + self +
                ", arg=" + arg +
                '}';
    }

    public Val instancer() {
        return self.asType().instancer();
    }

    public Val unpacker() {
        return self.asType().unpacker();
    }
}
