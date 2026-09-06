package org.fw.core.vit;

import org.fw.core.base.Val;
import org.fw.core.base.context.RtEnv;
import org.fw.core.state.obj.State;

import java.util.Objects;

public final class VitVal extends Vit {

    private final Val val;

    public VitVal(Val val) {
        this.val = Objects.requireNonNull(val);
    }

    public Val val() {
        return val;
    }

    @Override
    public Val eval(RtEnv rtEnv, State state) {
        return val;
    }

    @Override
    public boolean isConst() {
        return true;
    }

    @Override
    public boolean isPure() {
        return true;
    }

    @Override
    public String toString() {
        return "(VitVal " + val + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VitVal)) return false;
        VitVal vitVal = (VitVal) o;
        return Objects.equals(val, vitVal.val);
    }

    @Override
    public int hashCode() {
        return Objects.hash(val);
    }
}