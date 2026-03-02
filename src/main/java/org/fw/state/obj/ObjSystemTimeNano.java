package org.fw.state.obj;

import org.fw.base.Context;
import org.fw.base.Val;
import org.fw.lib.DIntFw;
import org.fw.lib.StrFw;

import java.util.Scanner;

public final class ObjSystemTimeNano extends AbstractObj {

    public ObjSystemTimeNano(Scope owner) {
        super(owner);
    }

    @Override
    public Val read(Context context) {
        return DIntFw.dint(System.nanoTime());
    }

    @Override
    public void write(Context context, Val x) {
        // nah
    }
}

