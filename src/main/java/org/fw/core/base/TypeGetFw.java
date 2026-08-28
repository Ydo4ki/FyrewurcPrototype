package org.fw.core.base;

import org.fw.core.FW;
import org.fw.core.contract.CallContract;

public final class TypeGetFw {
    public static final Val typeGet = FW.telephonist(
            (arg) -> arg.type().asVal()
            , CallContract.unknown()
    );
}
