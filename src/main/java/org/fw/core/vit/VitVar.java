package org.fw.core.vit;

import org.fw.core.abstrait.Value;
import org.fw.core.state.obj.State;

public final class VitVar extends Vit {

    VitVar() {
        //noinspection ConstantValue
        if (var != null) throw new UnsupportedOperationException();
    }

    @Override
    public Value eval(Value rtEnv, State state) {
        return rtEnv;
    }

    @Override
    public boolean isConst() {
        return false;
    }

    @Override
    public boolean isPure() {
        return true;
    }

    @Override
    public String toString() {
        return "(VitVar)";
    }
}