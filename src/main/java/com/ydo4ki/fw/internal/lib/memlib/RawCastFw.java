package com.ydo4ki.fw.internal.lib.memlib;

import org.fw.core.FW;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.lib.stdlib.WrapperTypeFw;

public final class RawCastFw {
    public static final Val rawcast = FW.telephonist_native(v -> FW.telephonist_native(targetType -> {
        Type originalType = v.getType();
        originalType = WrapperTypeFw.unwrapFully(originalType);
        if (originalType.asVal().getType() == ReifiedTypeFw.reifiedType) {
            throw new UnsupportedOperationException("todo");
        }
        return null;
    }));
}
