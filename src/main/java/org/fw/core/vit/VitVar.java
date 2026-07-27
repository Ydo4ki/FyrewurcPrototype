package org.fw.core.vit;

import org.fw.core.base.context.Context;
import org.fw.core.base.Val;

public final class VitVar extends Vit {

    VitVar() {
        if (var != null) throw new UnsupportedOperationException();
        // empty
    }

    @Override
    public Val eval(Context context) {
        return context.rtEnv().asVal();
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