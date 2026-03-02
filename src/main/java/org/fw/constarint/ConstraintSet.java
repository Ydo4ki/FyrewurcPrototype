package org.fw.constarint;

import org.fw.base.Context;
import org.fw.base.Val;

import java.util.Set;

public final class ConstraintSet { // OR
    Set<StrictConstraint> alternatives;

    public boolean test(Val val, Context context) {
        for (StrictConstraint alternative : alternatives) {
            if (alternative.test(val, context)) return true;
        }
        return false;
    }
}
