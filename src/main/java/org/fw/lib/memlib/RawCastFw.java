package org.fw.lib.memlib;

import org.fw.core.FW;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.lib.elib.WrapperTypeFw;

public final class RawCastFw {
    public static final Val rawcast = FW.telephonist(v -> FW.telephonist(targetType -> {
        Type originalType = v.type();
        originalType = WrapperTypeFw.unwrapFully(originalType);
        if (originalType.asVal().type() == ReifiedTypeFw.reifiedType) {
            throw new UnsupportedOperationException("todo");
        }
        return null;
    }));
}
