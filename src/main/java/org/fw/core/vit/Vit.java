package org.fw.core.vit;

import org.fw.core.FwUtils;
import org.fw.core.base.Context;
import org.fw.core.base.Val;
import org.fw.core.state.obj.Obj;
import org.fw.core.lib.state.OperationFw;

import java.util.Objects;
import java.util.Set;

// no side effects for now
public sealed interface Vit permits VitCall, VitInvoke, VitVal, VitVar {
    Vit var = new VitVar(); // ok it was kind of quick
    // but we'll need to do some cleanup
    
    static Vit simplify(Vit vit, Context context) {
        return switch (vit) {
            case VitVal _, VitVar _: yield vit;
            case VitCall(Vit func, Vit arg, _, _):
                func = simplify(func, context);
                arg = simplify(arg, context);
                if (func instanceof VitVal(Val f) && arg instanceof VitVal(Val argument)) {
                    yield val(f.call(argument, context));
                }
                yield new VitCall(func, arg);
            case VitInvoke vitInvoke:
                yield vitInvoke; // you can't really simplify this one without runtime context
        };
    }

    // simplifies and applies var value from the given context (so there won't be any VitVars in the resulting tree)
    static Vit reduce(Vit vit, Context context) {
        return switch (vit) {
            case VitVal _: yield vit;
            case VitVar _: yield val(context.rtEnv().asVal());
            case VitCall(Vit func, Vit arg, _, _):
                func = reduce(func, context);
                arg = reduce(arg, context);
                if (func instanceof VitVal(Val f) && arg instanceof VitVal(Val argument)) {
                    yield val(f.call(argument, context));
                }
                yield new VitCall(func, arg);
            case VitInvoke vitInvoke:
                yield vitInvoke.isConst()
                        ? vitInvoke
                        : new VitInvoke(reduce(vitInvoke.operation(), context));
        };
    }

    static Set<Obj> reads(Vit vit, Context context) {
//        if (!vit.isConst())
//            throw new IllegalArgumentException("Clean up: " + VitFw.wrap(vit).toExpr(new Context(RtEnv.unspecified, Scope.eternal())));
        return switch (vit) {
            case VitVal _, VitVar _ -> Set.of();
            case VitCall(Vit func, Vit arg, _, _) -> FwUtils.mergeImmut(reads(func, context), reads(arg, context));
            case VitInvoke vitInvoke ->
                    Objects.requireNonNull(OperationFw.unwrap(vitInvoke.operationVal(context))).reads(context);
        };
    }

    static Set<Obj> writes(Vit vit, Context context) {
//        if (!vit.isConst())
//            throw new IllegalArgumentException("Clean up");
        return switch (vit) {
            case VitVal _, VitVar _ -> Set.of();
            case VitCall(Vit func, Vit arg, _, _) -> FwUtils.mergeImmut(writes(func, context), writes(arg, context));
            case VitInvoke vitInvoke ->
                    Objects.requireNonNull(OperationFw.unwrap(vitInvoke.operationVal(context))).writes(context);
        };
    }

    Val eval(Context context);

    boolean isConst();

    boolean isPure();

    boolean isLocal(Context context);

    default Vit call(Vit arg) {
        return call(this, arg);
    }

    default Vit call(Val arg) {
        return call(this, arg);
    }

    static Vit val(Val val) {
        return new VitVal(val);
    }

    static Vit var(Val key) {
        return new VitVar().call(key);
    }

    static Vit call(Vit val, Vit arg) {
        return new VitCall(val, arg);
    }

    static Vit call(Val val, Val arg) {
        return call(val(val), val(arg));
    }

    static Vit call(Val val, Vit arg) {
        return call(val(val), arg);
    }

    static Vit call(Vit val, Val arg) {
        return call(val, val(arg));
    }

    static Vit invoke(Vit operation) {
        return new VitInvoke(operation);
    }
}

