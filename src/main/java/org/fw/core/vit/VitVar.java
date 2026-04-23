package org.fw.core.vit;

import org.fw.core.base.Context;
import org.fw.core.base.Val;

public record VitVar() implements Vit {

    @Override
    public Val eval(Context context) {
        return context.rtEnv().asVal();
//        if (key == null)
//        // I think we'll keep this for now
//        // and delete the key later
//        return context.rtEnv().get(key, context);
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
    public boolean isLocal(Context context) {
        return true;
    }

    @Override
    public String toString() {
//        if (key == null)
//        return "(VitVar " + key + ")";
        return "(VitVar)";
    }
}
