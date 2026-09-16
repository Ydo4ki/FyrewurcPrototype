package org.fw.core.state.obj;

import org.fw.core.abstrait.Value;

public interface State {
    Value asValHandle();
    State parent();
}