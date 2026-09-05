package org.fw.core.base.context;

import org.fw.core.FW;
import org.fw.core.commons.AbstractValAdapter;
import org.fw.core.base.Val;

public final class RtEnv extends AbstractValAdapter {
    @Deprecated
    public static final RtEnv unspecified = new RtEnv(FW.telephonist((arg) -> null));

    private RtEnv(Val val) {
        super(val);
    }

    public static RtEnv of(Val val) {
        return new RtEnv(val);
    }

    public Val get(Val key) {
        return asVal().call(key);
    }
}
