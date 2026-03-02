package org.fw.lib.constraint;

import org.fw.FW;
import org.fw.base.Val;

public final class ConstraintFw {
    public static final Val constr = FW.telephonist("@", (arg, context) -> {
        return Val.unspecified;
    });

    // ok actually maybe I should do errors first
    // otherwise this will be impossible to work with
}
