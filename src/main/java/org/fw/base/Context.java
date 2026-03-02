package org.fw.base;

import org.fw.state.obj.Scope;
import org.fw.vit.RtEnv;

public final class Context {
    private final RtEnv rtEnv;
    private final Scope scope;

    public Context(RtEnv rtEnv, Scope scope) {
        this.rtEnv = rtEnv;
        this.scope = scope;
    }

    public RtEnv rtEnv() {
        return rtEnv;
    }

    public Scope scope() {
        return scope;
    }
}
