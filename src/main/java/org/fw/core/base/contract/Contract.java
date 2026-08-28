package org.fw.core.base.contract;

import org.fw.core.base.Constraint;

public abstract class Contract {

    Contract() {}

    public abstract Constraint constraintOfResult();
}
