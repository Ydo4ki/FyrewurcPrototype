package org.fw.core.abstrait;

import org.fw.base.Val;

public final class ImpossibleValue implements Value {

    public static final ImpossibleValue value = new ImpossibleValue();

    private ImpossibleValue() {}

    @Override
    public Value call(Value arg) {
        return new LazyCallValue(this, arg);
    }

    @Override
    public boolean impliesEquality(Val val) {
        return false;
    }
}
