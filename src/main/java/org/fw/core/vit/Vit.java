package org.fw.core.vit;

import org.fw.core.base.context.Context;
import org.fw.core.base.Val;
import org.fw.core.base.context.RtEnv;
import org.fw.core.state.obj.State;

// no side effects for now
public abstract class Vit {

    public static final Vit var = new VitVar(); // ok it was kind of quick
    // but we'll need to do some cleanup

    public static Vit simplify(Vit vit) {
        return simplify0(vit);
    }

    private static Vit simplify0(Vit vit) {
        if (vit instanceof VitVal || vit instanceof VitVar) {
            return vit;
        }

        if (vit instanceof VitCall) {
            VitCall call = (VitCall) vit;

            Vit func = simplify(call.func());
            Vit arg = simplify(call.arg());

            if (func instanceof VitVal && arg instanceof VitVal) {
                Val f = ((VitVal) func).val();
                Val a = ((VitVal) arg).val();
                return val(f.call(a));
            }

            return new VitCall(func, arg);
        }

        if (vit instanceof VitInvoke) {
            return vit; // you can't really simplify this one without runtime context
        }

        throw new IllegalStateException("Unknown Vit: " + vit);
    }

    // simplifies and applies var value from the given context (so there won't be any VitVars in the resulting tree)
    public static Vit reduce(Vit vit, RtEnv rtEnv) {
        if (vit instanceof VitVal) {
            return vit;
        }

        if (vit instanceof VitVar) {
            return val(rtEnv.asVal());
        }

        if (vit instanceof VitCall) {
            VitCall call = (VitCall) vit;

            Vit func = reduce(call.func(), rtEnv);
            Vit arg = reduce(call.arg(), rtEnv);

            if (func instanceof VitVal && arg instanceof VitVal) {
                Val f = ((VitVal) func).val();
                Val a = ((VitVal) arg).val();
                return val(f.call(a));
            }

            return new VitCall(func, arg);
        }

        if (vit instanceof VitInvoke) {
            VitInvoke inv = (VitInvoke) vit;
            return inv.isConst()
                    ? inv
                    : new VitInvoke(reduce(inv.operation(), rtEnv));
        }

        throw new IllegalStateException("Unknown Vit: " + vit);
    }

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