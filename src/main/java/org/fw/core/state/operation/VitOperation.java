package org.fw.core.state.operation;

import org.fw.core.base.Val;
import org.fw.core.base.context.RtEnv;
import org.fw.core.state.obj.State;
import org.fw.core.vit.Vit;

import java.util.Objects;

public final class VitOperation extends Operation {
    private final Vit vit;
    private final RtEnv rtEnv; // ok storing this just seems easier

    VitOperation(Vit vit, RtEnv rtEnv) {
        this.vit = Objects.requireNonNull(vit);
        this.rtEnv = Objects.requireNonNull(rtEnv);
    }

    @Override
    public Val apply(State state) {
        return vit.eval(rtEnv, state);
    }

    public Vit vit() {
        return vit;
    }
}
