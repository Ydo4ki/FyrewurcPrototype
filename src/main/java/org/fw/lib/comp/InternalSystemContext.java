package org.fw.lib.comp;

import org.fw.base.Context;
import org.fw.state.obj.Scope;
import org.fw.vit.RtEnv;

public final class InternalSystemContext {
    public static final Context context = new Context(RtEnv.unspecified, Scope.eternal());
}
