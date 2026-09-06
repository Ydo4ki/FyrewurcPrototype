package org.fw.core.abstrait;

import org.fw.core.base.Type;

public interface TypedValue extends Value {
    Type getType();

    @Override
    default Value getTypeValue() {
        return getType().asVal();
    }
}
