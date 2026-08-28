package org.fw.core.state.operation;

import org.fw.core.FW;
import org.fw.core.commons.ValAdapter;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.base.Val;
import org.fw.core.base.context.RtEnv;
import org.fw.core.base.contract.CallContract;
import org.fw.core.base.contract.InvokeContract;
import org.fw.core.state.obj.State;
import org.fw.core.state.obj.AtomObj;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitInvoke;
import org.fw.lib.stdlib.state.OperationFw;

public abstract class Operation implements ValAdapter {
    // a Val symbolizing successful completion of the operation
    // basically the same as the old 'unspecified'
    // just without negative connotation
    // upd: ok nevermind it has kinda negative connotation now since unspecified is now very strict
    // maybe I should make a separate unspecified type for failed operations :hmm:
    public static final Val unit = FW.telephonist((arg) -> Operation.unit, CallContract.constant(() -> Operation.unit));

    public abstract Val apply(State state);

    public abstract InvokeContract contract();

    private final Val asVal;
    private Boolean isPure = null;

    protected Operation() {
        this.asVal = Val.of(OperationFw.operation, this);
    }

    public static Operation read(AtomObj obj) {
        return new ReadOperation(obj);
    }

    public static Operation write(AtomObj obj, Val x) {
        return new WriteOperation(obj, x);
    }

    public static Operation vit(Vit vit, RtEnv rtEnv) {
        if (vit instanceof VitInvoke) {
            Vit v = ((VitInvoke) vit).operation();
            if (v.isConst() && v.isPure()) {
                return OperationFw.unwrap(v.eval());
            }
        }
        return new VitOperation(vit, rtEnv);
    }

    public static Operation pure(Val val) {
        return new VitOperation(Vit.val(val), RtEnv.unspecified);
    }

    public Expr toExpr() {
        return ExprList.of(BracketsTypes.braces);
    }

    @Override
    public final Val asVal() {
        return asVal;
    }

    public final boolean operationAreYouPureQuestionMark() {
        if (isPure == null) {
            isPure = contract().isPure();
        }
        return isPure;
    }
}

