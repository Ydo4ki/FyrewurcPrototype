package org.fw.core.abstrait;

import org.fw.base.Type;
import org.fw.base.Val;

public interface TypedValue extends Value {
    Type getType();

    @Override
    default Val getTypeValue() {
        return getType().asVal();
    }
}
