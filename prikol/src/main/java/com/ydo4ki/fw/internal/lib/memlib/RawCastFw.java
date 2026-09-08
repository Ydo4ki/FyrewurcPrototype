package com.ydo4ki.fw.internal.lib.memlib;

import org.fw.core.FW;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.std.WrapperTypeFw;

public final class RawCastFw {
    public static final Val rawcast = FW.lambda_native(v -> FW.lambda_native(targetType -> {
        Type originalType = v.getType();
        originalType = WrapperTypeFw.unwrapFully(originalType);
        if (originalType.asVal().getType() == ReifiedTypeFw.reifiedType) {
            throw new UnsupportedOperationException("todo");
        }
        return null;
    }));
}
