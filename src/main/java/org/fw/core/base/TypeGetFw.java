package org.fw.core.base;

import org.fw.core.FW;
import org.fw.lib.stdlib.constraint._Constraint;

public final class TypeGetFw {
    public static final Val typeGet = FW.telephonist(
            (arg) -> arg.type().asVal()
            , (arg) -> _Constraint.free
    );
}
