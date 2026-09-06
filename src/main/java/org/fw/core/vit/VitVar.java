package org.fw.core.vit;

import org.fw.core.abstrait.Value;
import org.fw.core.state.obj.State;

public final class VitVar extends Vit {

    VitVar() {
        if (var != null) throw new UnsupportedOperationException();
        // empty
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