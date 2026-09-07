package org.fw.core.base;

import org.fw.core.FW;

public final class TypeGetFw {
    public static final Val typeGet = FW.telephonist_native_standalone("type-get", (arg) -> arg.getType().asVal());
}
