package org.fw.lib.stdlib.state;

import org.fw.core.FW;
import org.fw.core.base.Val;
import org.fw.core.base.BoolFw;
import org.fw.core.contract.InvokeContract;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;
import org.fw.core.state.operation.OperationFw;

import static org.fw.core.FW.telephonist;

// it's not like this can't be implemented on the language itself, this just seems easier
public final class IfOperation extends Operation {
    public static final Val _If = FW.telephonist((condition) -> {
        if (condition.type() != OperationFw.operation)
            return null;

        return FW.telephonist((ifTrue) -> {
            if (ifTrue.type() != OperationFw.operation)
                return null;

            return FW.telephonist((ifFalse) -> {
                if (ifFalse.type() != OperationFw.operation)
                    return null;

                return new IfOperation(condition._unpack(), ifTrue._unpack(), ifFalse._unpack()).asVal();
            });
        });
    });
    private final Operation condition;
    private final Operation ifTrue;
    private final Operation ifFalse;

    public IfOperation(Operation condition, Operation ifTrue, Operation ifFalse) {
        this.condition = condition;
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
    }

    @Override
    public Val apply(State state) {
        if (condition.apply(state) == BoolFw._true) {
            return ifTrue.apply(state);
        } else {
            return ifFalse.apply(state);
        }
    }

    @Override
    public InvokeContract contract() {
        return InvokeContract.unknown();
    }

    // todo:
//    @Override
//    protected boolean isPure0() {
//        return condition.operationAreYouPureQuestionMark() && ifTrue.operationAreYouPureQuestionMark() && ifFalse.operationAreYouPureQuestionMark();
//    }
}
