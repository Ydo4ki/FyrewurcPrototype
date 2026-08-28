package org.fw.core.vit;

import org.fw.core.base.Val;
import org.fw.core.base.context.RtEnv;
import org.fw.core.base.contract.CallContract;
import org.fw.core.state.obj.State;

public final class VitVar extends Vit {

    VitVar() {
        if (var != null) throw new UnsupportedOperationException();
        // empty
    }

    @Override
    public Val eval(RtEnv rtEnv, State state) {
        return rtEnv.asVal();
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
    public CallContract evalContract() {
        return contract;
    }

    private static final CallContract contract = CallContract.c(c -> c);

    @Override
    public String toString() {
        return "(VitVar)";
    }
}