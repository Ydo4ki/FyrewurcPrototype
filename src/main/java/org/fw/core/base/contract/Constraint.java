package org.fw.core.base.contract;

import org.fw.core.base.*;
import org.fw.core.base.context.RtEnv;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitUtils;

import java.util.Objects;

import static org.fw.core.FW.symbol;

public final class Constraint {
    private final Vit a;

    public static final Constraint free = Constraint.of(Vit.val(BoolFw._true));

    public static Constraint of(Vit vit) {
        return new Constraint(vit);
    }

    public static Constraint type(Type type) {
        return of(Vit.val(EqFw.eq).call(Vit.val(type.asVal())).call(Vit.call(TypeGetFw.typeGet, Vit.var)));
    }

    public static Constraint equals(Val val) {
        return of(Vit.val(EqFw.eq).call(Vit.val(val)).call(Vit.var));
    }

    private Constraint(Vit a) {
        this.a = a;
    }

    public boolean check(Val val) {
        return a.eval(RtEnv.of(val)).equals(BoolFw._true);
    }

    // constraint of the call result
    public Constraint call(Constraint constraint) {
        return of(
                VitUtils.substitude(a, constraint.a)
        );
    }

    public Constraint call(Val val) {
        return call(equals(val));
    }

    public Constraint get(String property) {
        return call(symbol(property));
    }

    public boolean implies(Constraint constraint) {
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

    public Constraint typeConstraint() {
        return equals(TypeGetFw.typeGet).call(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Constraint that = (Constraint) o;
        return Objects.equals(a, that.a);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(a);
    }
}