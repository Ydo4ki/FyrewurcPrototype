package org.fw.core.lib.constraint;

import org.fw.core.base.BoolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.base.ValsFw;
import org.fw.core.base.context.RtEnv;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitUtils;

public final class _Constraint {
    private final Vit a;
    private final Vit b;

    public static final _Constraint free = _Constraint.of(Vit.val(BoolFw._true));

    public static _Constraint of(Vit vit) {
        return of(vit, Vit.val(BoolFw._true));
    }

    public static _Constraint of(Type type) {
        return of(Vit.val(type.asVal()), Vit.call(ValsFw.typeGet, Vit.var));
    }

    public static _Constraint equals(Val val) {
        return of(Vit.val(val), Vit.var);
    }

    public static _Constraint of(Vit a, Vit b) {
        return new _Constraint(a, b);
    }

    private _Constraint(Vit a, Vit b) {
        if (a.equals(b)) {
            a = Vit.var;
            b = a;
        }
        this.a = a;
        this.b = b;
    }

    public boolean check(Val val) {
        return a.eval(RtEnv.of(val)).equals(b.eval(RtEnv.of(val)));
    }

    // constraint of call result
    public _Constraint call(_Constraint constraint) {
        return of(
                VitUtils.substitude(a, constraint.a),
                VitUtils.substitude(b, constraint.b)
        );
    }

    public boolean implies(_Constraint constraint) {
        if (constraint.isFree()) {
            return true;
        } else if (this.isFree()) {
            return false;
        }

        if (this.equals(constraint)) {
            return true;
        }

        return false;
    }

    private boolean isFree() {
        return a.equals(b);
    }

    public _Constraint typeConstraint() {
        return equals(ValsFw.typeGet).call(this);
    }
}