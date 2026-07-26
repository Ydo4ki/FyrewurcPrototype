package org.fw.core.lib.state;

import org.fw.core.FW;
import org.fw.core.base.Type;
import org.fw.core.base.Val;

public class LaserPointerFw {
    public static final Type laserPointer = FW.telephonist("LaserPointer", (arg, context) -> {
        return Val.unspecified;
    }).asType();
}
