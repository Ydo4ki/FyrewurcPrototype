package org.fw.core.base;

import org.fw.core.state.obj.State;
import org.fw.core.vit.RtEnv;

public final class Context {
    private final RtEnv rtEnv;
    private final State state;

    public Context(RtEnv rtEnv, State state) {
        this.rtEnv = rtEnv;
        this.state = state;
    }

    public RtEnv rtEnv() {
        return rtEnv;
    }

    public State state() {
        return state;
    }


    public static final Context outOf = new Context(RtEnv.unspecified, State.eternal());
}
