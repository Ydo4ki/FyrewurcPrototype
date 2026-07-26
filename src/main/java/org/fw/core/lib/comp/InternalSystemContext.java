package org.fw.core.lib.comp;

import org.fw.core.base.Context;
import org.fw.core.state.obj.State;
import org.fw.core.vit.RtEnv;

public final class InternalSystemContext {
    public static final Context context = new Context(RtEnv.unspecified, State.eternal());
}
