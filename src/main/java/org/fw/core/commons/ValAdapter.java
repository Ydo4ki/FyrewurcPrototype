package org.fw.core.commons;

import org.fw.core.abstrait.Value;
import org.fw.core.base.Val;

public interface ValAdapter {
    Val asVal();

    default Value get(String property) {
        return asVal().get(property);
    }
}
