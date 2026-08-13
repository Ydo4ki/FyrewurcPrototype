package org.fw.core.vit;

import org.fw.core.base.Val;
import org.fw.core.base.context.RtEnv;
import org.fw.core.state.obj.State;

// no side effects for now
public abstract class Vit {

    public static final Vit var = new VitVar(); // ok it was kind of quick
    // but we'll need to do some cleanup

    public final Val eval() {
        return eval(RtEnv.unspecified);
    }

    public final Val eval(RtEnv rtEnv) {
        return State.performAndDie(state -> eval(rtEnv, state));
    }

    public abstract Val eval(RtEnv rtEnv, State state);

    public abstract boolean isConst();

    public abstract boolean isPure();

    public Vit call(Vit arg) {
        return call(this, arg);
    }

    public Vit call(Val arg) {
        return call(this, arg);
    }

    public static Vit val(Val val) {
        return new VitVal(val);
    }

    @Deprecated
    public static Vit var(Val key) {
        return var.call(key);
    }

    public static Vit call(Vit val, Vit arg) {
        return new VitCall(val, arg);
    }

    public static Vit call(Val val, Val arg) {
        return call(val(val), val(arg));
    }

    public static Vit call(Val val, Vit arg) {
        return call(val(val), arg);
    }

    public static Vit call(Vit val, Val arg) {
        return call(val, val(arg));
    }

    public static Vit invoke(Vit operation) {
        return new VitInvoke(operation);
    }
}