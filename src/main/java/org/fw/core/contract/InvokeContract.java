package org.fw.core.contract;

public final class InvokeContract {
    private final _Constraint resultingConstraint;
    private final boolean modifiesState;
    private final boolean readsState;

    InvokeContract(_Constraint resultingConstraint, boolean modifiesState, boolean readsState) {
        this.resultingConstraint = resultingConstraint;
        this.modifiesState = modifiesState;
        this.readsState = readsState;
    }

    public boolean doesModifyState() {
        return modifiesState;
    }

    public boolean doesReadState() {
        return readsState;
    }

    public _Constraint getResultingConstraint() {
        return resultingConstraint;
    }
}
