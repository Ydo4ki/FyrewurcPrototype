package org.fw.core.base;

import org.fw.core.FW;
import org.fw.core.contract.CallContract;
import org.fw.core.contract._Constraint;

public final class TypeGetFw {
    public static final Val typeGet = FW.telephonist(
            (arg) -> arg.type().asVal()
            , CallContract.unknown()
    );
}
