package org.fw.core.vit;

import org.fw.core.util.FwUtils;
import org.fw.core.base.Context;
import org.fw.core.base.Val;
import org.fw.core.state.obj.Obj;
import org.fw.core.lib.state.OperationFw;

import java.util.Objects;
import java.util.Set;

// no side effects for now
public abstract class Vit {

    public static final Vit var = new VitVar(); // ok it was kind of quick
    // but we'll need to do some cleanup

    public static Vit simplify(Vit vit, Context context) {
        if (vit instanceof VitVal || vit instanceof VitVar) {
            return vit;
        }

        if (vit instanceof VitCall) {
            VitCall call = (VitCall) vit;

            Vit func = simplify(call.func(), context);
            Vit arg = simplify(call.arg(), context);

            if (func instanceof VitVal && arg instanceof VitVal) {
                Val f = ((VitVal) func).val();
                Val a = ((VitVal) arg).val();
                return val(f.call(a, context));
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
                return val(f.call(a, context));
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

    public static Set<Obj> reads(Vit vit, Context context) {
//        if (!vit.isConst())
//            throw new IllegalArgumentException("Clean up: " + VitFw.wrap(vit).toExpr(new Context(RtEnv.unspecified, Scope.eternal())));
        if (vit instanceof VitVal || vit instanceof VitVar) {
            return java.util.Collections.emptySet();
        }

        if (vit instanceof VitCall) {
            VitCall call = (VitCall) vit;
            return FwUtils.mergeImmut(
                    reads(call.func(), context),
                    reads(call.arg(), context)
            );
        }

        if (vit instanceof VitInvoke) {
            VitInvoke inv = (VitInvoke) vit;
            return Objects.requireNonNull(
                    OperationFw.unwrap(inv.operationVal(context))
            ).reads(context);
        }

        throw new IllegalStateException("Unknown Vit: " + vit);
    }

    public static Set<Obj> writes(Vit vit, Context context) {
//        if (!vit.isConst())
//            throw new IllegalArgumentException("Clean up");
        if (vit instanceof VitVal || vit instanceof VitVar) {
            return java.util.Collections.emptySet();
        }

        if (vit instanceof VitCall) {
            VitCall call = (VitCall) vit;
            return FwUtils.mergeImmut(
                    writes(call.func(), context),
                    writes(call.arg(), context)
            );
        }

        if (vit instanceof VitInvoke) {
            VitInvoke inv = (VitInvoke) vit;
            return Objects.requireNonNull(
                    OperationFw.unwrap(inv.operationVal(context))
            ).writes(context);
        }

        throw new IllegalStateException("Unknown Vit: " + vit);
    }

    public abstract Val eval(Context context);

    public abstract boolean isConst();

    public abstract boolean isPure();

    public abstract boolean isLocal(Context context);

    public Vit call(Vit arg) {
        return call(this, arg);
    }

    public Vit call(Val arg) {
        return call(this, arg);
    }

    public static Vit val(Val val) {
        return new VitVal(val);
    }

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