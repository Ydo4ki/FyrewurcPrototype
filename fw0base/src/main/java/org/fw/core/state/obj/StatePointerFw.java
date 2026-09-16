package org.fw.core.state.obj;

import org.fw.core.FW;
import org.fw.base.CallFw;
import org.fw.base.SymbolFw;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.core.state.operation.GetLocalStateOperation;
import org.fw.core.util.FwUtils;

public final class StatePointerFw {
    public static final Val statePointer = GetLocalStateOperation.getInstance().asVal();
}

