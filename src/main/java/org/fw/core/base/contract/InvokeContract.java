package org.fw.core.base.contract;

import com.sun.javafx.scene.control.behavior.OptionalBoolean;
import org.fw.core.base.Constraint;
import org.fw.core.state.obj.Obj;

public final class InvokeContract {
    private static final InvokeContract UNKNOWN = new InvokeContract(Constraint.free, true, true, OptionalBoolean.ANY);

    public static InvokeContract unknown() {
        return UNKNOWN;
    }

    private final Constraint resultingConstraint;
    private final boolean modifiesState;
    private final boolean readsState;
    private final OptionalBoolean returnsBrandNew;


    InvokeContract(Constraint resultingConstraint, boolean mayModifyState, boolean mayReadState, OptionalBoolean returnsBrandNew) {
        this.resultingConstraint = resultingConstraint;
        this.modifiesState = mayModifyState;
        this.readsState = mayReadState;
        this.returnsBrandNew = returnsBrandNew;
    }

    public static InvokeContract returnsBrandNew(Constraint resultingConstraint, boolean mayModifyState, boolean mayReadState) {
        return new InvokeContract(resultingConstraint, mayModifyState, mayReadState, OptionalBoolean.TRUE);
    }

    public static InvokeContract readsOnly(Obj obj) {
        return new InvokeContract(Constraint.free, false, true, OptionalBoolean.FALSE);
    }

    public static InvokeContract writesOnly(Constraint constraint, Obj obj) {
        return new InvokeContract(constraint, true, false, OptionalBoolean.FALSE);
    }

    public boolean mayModifyState() {
        return modifiesState;
    }

    public boolean mayReadState() {
        return readsState;
    }

    public Constraint getResultingConstraint() {
        return resultingConstraint;
    }

    public boolean isPure() {
        return !mayModifyState() && !mayReadState();
    }
}
