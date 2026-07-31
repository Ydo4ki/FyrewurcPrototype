package org.fw.core.vit;

import org.fw.core.base.context.Context;
import org.fw.core.base.Val;

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
    public static Vit reduce(Vit vit, Context context) {
        if (vit instanceof VitVal) {
            return vit;
        }

        if (vit instanceof VitVar) {
            return val(context.rtEnv().asVal());
        }

        if (vit instanceof VitCall) {
            VitCall call = (VitCall) vit;

            Vit func = reduce(call.func(), context);
            Vit arg = reduce(call.arg(), context);

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
                    : new VitInvoke(reduce(inv.operation(), context));
        }

        throw new IllegalStateException("Unknown Vit: " + vit);
    }

    public abstract Val eval(Context context);

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