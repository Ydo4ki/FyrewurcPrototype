package org.fw.vit;

import org.fw.adapter.AbstractValAdapted;
import org.fw.base.Context;
import org.fw.base.Val;

public final class RtEnv extends AbstractValAdapted {

    public static final RtEnv unspecified = new RtEnv(Val.unspecified);

    private RtEnv(Val val) {
        super(val);
    }

    public static RtEnv of(Val val) {
        if (val == Val.unspecified)
            return unspecified;
        return new RtEnv(val);
    }

    public Val get(Val key, Context context) {
        return asVal().call(key, context);
    }
}
