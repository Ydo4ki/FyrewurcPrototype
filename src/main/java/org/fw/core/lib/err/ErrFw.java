package org.fw.core.lib.err;

import org.fw.core.util.FwUtils;
import org.fw.core.Main;
import org.fw.core.base.Context;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.comp.InternalSystemContext;
import org.fw.core.vit.RtEnv;

import java.io.File;
import java.io.IOException;

import static org.fw.core.FW.symbol;

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
