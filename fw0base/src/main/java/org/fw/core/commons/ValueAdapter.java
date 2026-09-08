package org.fw.core.commons;

import org.fw.core.abstrait.Value;

public interface ValueAdapter {
    Value asValue();

    default Value get(String property) {
        return asValue().get(property);
    }
}
