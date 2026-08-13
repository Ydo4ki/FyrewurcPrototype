package org.fw.core.commons;

public interface PureCallable<T extends PureCallable<T>> {
    T call(T arg);
}
