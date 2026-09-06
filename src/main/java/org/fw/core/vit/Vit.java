package org.fw.core.vit;

import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import org.fw.core.base.Val;
import org.fw.core.base.context.RtEnv;
import org.fw.core.state.obj.State;

public abstract class Vit {

    @SuppressWarnings("StaticInitializerReferencesSubClass")
    public static final Vit var = new VitVar();

    public final Val eval() {
        return eval(RtEnv.unspecified.asVal());
    }

    public final Val eval(Val rtEnv) {
        return (Val) eval((Value) rtEnv);
    }

    public final Value eval(Value rtEnv) {
        return State.performAndDie(state -> eval(rtEnv, state));
    }

    public final Val eval(Val rtEnv, State state) {
        return (Val) eval((Value) rtEnv, state);
    }

    public abstract Value eval(Value rtEnv, State state);

    public abstract boolean isConst();

    public abstract boolean isPure();

    public final Vit call(Vit arg) {
        return call(this, arg);
    }

    public final Vit call(Value arg) {
        return call(this, arg);
    }

    public static Vit val(Value val) {
        return new VitVal(val);
    }

    public static Vit call(Vit val, Vit arg) {
        return new VitCall(val, arg);
    }

    public static Vit call(Value val, Value arg) {
        return call(val(val), val(arg));
    }

    public static Vit call(Value val, Vit arg) {
        return call(val(val), arg);
    }

    public static Vit call(Vit val, Value arg) {
        return call(val, val(arg));
    }

    public static Vit invoke(Vit operation) {
        return new VitInvoke(operation);
    }

    public final Val asLambdaVal() {
        return FW.telephonist((env) -> State.performAndDie(scope ->
                this.eval(env, scope)));
    }
}