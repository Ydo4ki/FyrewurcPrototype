package org.fw.core.commons;

import org.fw.core.base.Val;

public interface ValAdapter {
    Val asVal();

    default Val get(String property) {
        return asVal().get(property);
    }
}
