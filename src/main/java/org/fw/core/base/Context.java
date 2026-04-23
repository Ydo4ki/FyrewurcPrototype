package org.fw.core.base;

import org.fw.core.state.obj.Scope;
import org.fw.core.vit.RtEnv;

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


    public static final Context blank = new Context(RtEnv.unspecified, Scope.eternal());
}
