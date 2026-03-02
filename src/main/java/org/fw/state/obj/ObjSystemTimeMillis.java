package org.fw.state.obj;

import org.fw.base.Context;
import org.fw.base.Val;
import org.fw.lib.DIntFw;

public final class ObjSystemTimeMillis extends AbstractObj {

    public ObjSystemTimeMillis(Scope owner) {
        super(owner);
    }

    @Override
    public Val read(Context context) {
        return DIntFw.dint(System.currentTimeMillis());
    }

    @Override
    public void write(Context context, Val x) {
        // nah
    }
}


