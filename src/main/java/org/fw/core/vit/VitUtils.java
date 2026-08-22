package org.fw.core.vit;

import org.fw.core.base.Val;
import org.fw.core.base.context.RtEnv;

public final class VitUtils {
    public static Vit substitude(Vit vit, Vit replaceVarWith) {
        if (vit instanceof VitVal) {
            return vit;
        }

        if (vit instanceof VitVar) {
            return replaceVarWith;
        }

        if (vit instanceof VitCall) {
            VitCall call = (VitCall) vit;

            Vit func = substitude(call.func(), replaceVarWith);
            Vit arg = substitude(call.arg(), replaceVarWith);

            if (func instanceof VitVal && arg instanceof VitVal) {
                Val f = ((VitVal) func).val();
                Val a = ((VitVal) arg).val();
                return Vit.val(f.call(a));
            }

            return new VitCall(func, arg);
        }

        if (vit instanceof VitInvoke) {
            VitInvoke inv = (VitInvoke) vit;
            return inv.isConst()
                    ? inv
                    : new VitInvoke(substitude(inv.operation(), replaceVarWith));
        }

        throw new IllegalStateException("Unknown Vit: " + vit);
    }

    // simplifies and applies var value from the given context (so there won't be any VitVars in the resulting tree)
    public static Vit reduce(Vit vit, RtEnv rtEnv) {
        return VitUtils.substitude(vit, Vit.val(rtEnv.asVal()));
    }

    public static Vit simplify(Vit vit) {
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
                return Vit.val(f.call(a));
            }

            return new VitCall(func, arg);
        }

        if (vit instanceof VitInvoke) {
            return vit; // you can't really simplify this one without runtime context
        }

        throw new IllegalStateException("Unknown Vit: " + vit);
    }
}
