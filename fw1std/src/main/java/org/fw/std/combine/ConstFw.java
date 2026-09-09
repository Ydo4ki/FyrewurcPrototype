package org.fw.std.combine;

import org.fw.base.Val;
import org.fw.core.FW;

public final class ConstFw {
    public static final Val _const = FW.lambda("const", c -> FW.lambda(arg -> c));
}
