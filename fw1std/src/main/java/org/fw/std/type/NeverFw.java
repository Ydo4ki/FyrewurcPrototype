package org.fw.std.type;

import org.fw.base.Type;
import org.fw.core.FW;

// canonical representation of a type with no instances
public final class NeverFw {
    public static final Type never = FW.telephonist(d -> null).asType();
}
