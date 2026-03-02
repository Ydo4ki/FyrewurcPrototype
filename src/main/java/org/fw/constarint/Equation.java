package org.fw.constarint;

import org.fw.base.Context;
import org.fw.base.Val;

public record Equation(Term right, Term left) {
    public boolean test(Val val, Context context) {
        Val leftVal = left.evaluate(val, context);
        Val rightVal = right.evaluate(val, context);
        return leftVal.equals(rightVal);
    }
}
