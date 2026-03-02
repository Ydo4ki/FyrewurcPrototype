package org.fw.lib.err;

import org.fw.FwUtils;
import org.fw.Main;
import org.fw.base.Context;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.lib.comp.InternalSystemContext;
import org.fw.vit.RtEnv;

import java.io.File;
import java.io.IOException;

import static org.fw.FW.symbol;

public final class ErrFw {

    public static final Context context = new Context(RtEnv.unspecified, InternalSystemContext.context.scope());
    public static final Val errs;

    static {
        try {
            errs = FwUtils.getValueFromFile(new File("int/errs"), Main.internalCompEnv, context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static final Type unspecifiedCall = errs.call(symbol("unspecified-call-err"), context).asType();
}
