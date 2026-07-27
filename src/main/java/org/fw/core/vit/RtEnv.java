package org.fw.core.vit;

import org.fw.core.adapter.AbstractValAdapted;
import org.fw.core.base.Context;
import org.fw.core.base.Unspecified;
import org.fw.core.base.Val;

public final class RtEnv extends AbstractValAdapted {

    public static final RtEnv unspecified = new RtEnv(Unspecified.unspecified);

    private RtEnv(Val val) {
        super(val);
    }

    public static RtEnv of(Val val) {
        if (val == Unspecified.unspecified)
            return unspecified;
        return new RtEnv(val);
    }

    public Val get(Val key, Context context) {
        return asVal().call(key, context);
    }
}
