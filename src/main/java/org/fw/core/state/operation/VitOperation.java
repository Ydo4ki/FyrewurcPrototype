package org.fw.core.state.operation;

import org.fw.core.base.context.Context;
import org.fw.core.base.Val;
import org.fw.core.base.context.RtEnv;
import org.fw.core.vit.Vit;

import java.util.Objects;

public final class VitOperation extends Operation {
    private final Vit vit;
    private final RtEnv rtEnv;

    VitOperation(Vit vit, RtEnv rtEnv) {
        this.vit = Objects.requireNonNull(vit);
        this.rtEnv = Objects.requireNonNull(rtEnv);
    }

    @Override
    public Val execute(Context context) {
        return vit.eval(new Context(rtEnv, context.state()));
    }

    @Override
    protected boolean isPure0() {
        return vit.isPure();
    }

    public Vit vit() {
        return vit;
    }
}
