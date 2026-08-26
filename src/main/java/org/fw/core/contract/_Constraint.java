package org.fw.core.contract;

import org.fw.core.base.*;
import org.fw.core.base.context.RtEnv;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitUtils;

public final class _Constraint {
    private final Vit a;

    public static final _Constraint free = _Constraint.of(Vit.val(BoolFw._true));

    public static _Constraint of(Vit vit) {
        return new _Constraint(vit);
    }

    public static _Constraint of(Type type) {
        return of(Vit.val(EqFw.eq).call(Vit.val(type.asVal())).call(Vit.call(TypeGetFw.typeGet, Vit.var)));
    }

    public static _Constraint equals(Val val) {
        return of(Vit.val(EqFw.eq).call(Vit.val(val)).call(Vit.var));
    }

    private _Constraint(Vit a) {
        this.a = a;
    }

    public boolean check(Val val) {
        return a.eval(RtEnv.of(val)).equals(BoolFw._true);
    }

    // constraint of the call result
    public _Constraint call(_Constraint constraint) {
        return of(
                VitUtils.substitude(a, constraint.a)
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
        return a.equals(Vit.val(BoolFw._true));
    }

    public _Constraint typeConstraint() {
        return equals(TypeGetFw.typeGet).call(this);
    }
}