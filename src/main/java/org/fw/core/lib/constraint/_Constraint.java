package org.fw.core.lib.constraint;

import org.fw.core.base.BoolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.base.ValsFw;
import org.fw.core.vit.Vit;

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

    public _Constraint call(_Constraint constraint) {
        return free;
    }

    public boolean implies(_Constraint constraint) {
        return false;
    }

    public _Constraint typeConstraint() {
        return equals(ValsFw.typeGet).call(this);
    }
}

