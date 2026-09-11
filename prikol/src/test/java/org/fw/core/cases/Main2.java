package org.fw.core.cases;

import org.fw.base.EqFw;
import org.fw.base.Val;
import org.fw.core.abstrait.ConstraintValue;
import org.fw.core.abstrait.Value;
import org.fw.core.constraint.Constraint;

public final class Main2 {
    public static void main(String[] args) {
        Val fn = EqFw.eq;
        Value ret = fn
                .call(new ConstraintValue(Constraint.free))
                .call(new ConstraintValue(Constraint.free))
                ;
        System.out.println(ret);
    }
}
