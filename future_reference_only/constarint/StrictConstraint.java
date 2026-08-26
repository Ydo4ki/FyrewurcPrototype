package org.fw.constarint;

import org.fw.base.Context;
import org.fw.base.Val;

import java.util.Set;

public final class StrictConstraint { // AND
    Set<Equation> equations;

    public boolean test(Val val, Context context) {
        for (Equation alternative : equations) {
            if (!alternative.test(val, context)) return false;
        }
        return true;
    }
}

