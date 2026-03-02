package org.fw.vit;

import org.fw.base.Context;
import org.fw.base.Val;

public record VitVal(Val val) implements Vit {
    @Override
    public Val eval(Context context) {
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
    public boolean isLocal(Context context) {
        return true;
    }

    @Override
    public String toString() {
        return "(VitVal " + val + ")";
    }
}
