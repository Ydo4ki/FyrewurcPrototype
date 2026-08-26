package org.fw.core.contract;

import com.sun.javafx.scene.control.behavior.OptionalBoolean;
import org.fw.core.state.obj.Obj;
import org.fw.core.state.obj.ValObj;

public final class InvokeContract {
    private static final InvokeContract UNKNOWN = new InvokeContract(_Constraint.free, true, true, OptionalBoolean.ANY);

    public static InvokeContract unknown() {
        return UNKNOWN;
    }

    private final _Constraint resultingConstraint;
    private final boolean modifiesState;
    private final boolean readsState;
    private final OptionalBoolean returnsBrandNew;


    InvokeContract(_Constraint resultingConstraint, boolean mayModifyState, boolean mayReadState, OptionalBoolean returnsBrandNew) {
        this.resultingConstraint = resultingConstraint;
        this.modifiesState = mayModifyState;
        this.readsState = mayReadState;
        this.returnsBrandNew = returnsBrandNew;
    }

    public static InvokeContract returnsBrandNew(_Constraint resultingConstraint, boolean mayModifyState, boolean mayReadState) {
        return new InvokeContract(resultingConstraint, mayModifyState, mayReadState, OptionalBoolean.TRUE);
    }

    public static InvokeContract readsOnly(Obj obj) {
        return new InvokeContract(_Constraint.free, false, true, OptionalBoolean.FALSE);
    }

    public static InvokeContract writesOnly(_Constraint constraint, Obj obj) {
        return new InvokeContract(constraint, true, false, OptionalBoolean.FALSE);
    }

    public boolean mayModifyState() {
        return modifiesState;
    }

    public boolean mayReadState() {
        return readsState;
    }

    public _Constraint getResultingConstraint() {
        return resultingConstraint;
    }

    public boolean isPure() {
        return !mayModifyState() && !mayReadState();
    }
}
