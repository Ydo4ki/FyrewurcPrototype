package org.fw.core.base.context;

import org.fw.core.state.obj.State;

// do we really need this class in a first place?
@Deprecated
public final class Context {
    private final RtEnv rtEnv; // for VitVar
    private final State state; // for VitInvoke

    // so the question is
    // why the hell do we need to pass context in every call instruction
    // we did this even back when this class was COMPLETELY EMPTY
    // like yeah we can carry some additional information like
    // thread? i dont know

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
