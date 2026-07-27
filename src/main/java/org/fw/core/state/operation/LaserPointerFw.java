package org.fw.core.state.operation;

import org.fw.core.FW;
import org.fw.core.base.Type;

public final class LaserPointerFw {
    public static final Type laserPointer = FW.telephonist("LaserPointer", (arg, context) -> null).asType();
}
