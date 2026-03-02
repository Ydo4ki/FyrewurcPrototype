package org.fw;

import java.util.function.Supplier;

public final class LazyObj<T> implements Supplier<T> {
    private final Supplier<T> supplier;
    private T value;

    public LazyObj(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public static <T> LazyObj<T> of(Supplier<T> supplier) {
        if (supplier instanceof LazyObj<T>)
            return (LazyObj<T>) supplier;
        return new LazyObj<>(supplier);
    }

    @Override
    public T get() {
        if (value == null) value = supplier.get();
        return value;
    }
}
