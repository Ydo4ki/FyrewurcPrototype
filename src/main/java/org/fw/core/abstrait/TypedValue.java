package org.fw.core.abstrait;

import org.fw.core.base.Type;
import org.fw.core.base.Val;

public interface TypedValue extends Value {
    Type getType();

    @Override
    default Val getTypeValue() {
        return getType().asVal();
    }
}
