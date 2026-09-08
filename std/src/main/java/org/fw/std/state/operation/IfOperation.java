package org.fw.std.state.operation;

import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import org.fw.base.Val;
import org.fw.base.BoolFw;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;
import org.fw.base.OperationFw;

import static org.fw.core.FW.telephonist_native;

// it's not like this can't be implemented on the language itself, this just seems easier
public final class IfOperation extends Operation {
    public static final Val _If = FW.telephonist_native((condition) -> {
        if (condition.getType() != OperationFw.operation)
            return null;

        return FW.telephonist_native((ifTrue) -> {
            if (ifTrue.getType() != OperationFw.operation)
                return null;

            return FW.telephonist_native((ifFalse) -> {
                if (ifFalse.getType() != OperationFw.operation)
                    return null;

                return new IfOperation(condition._UNPACK_(), ifTrue._UNPACK_(), ifFalse._UNPACK_()).asVal();
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
    public Value apply(State state) {
        if (condition.apply(state) == BoolFw._true) {
            return ifTrue.apply(state);
        } else {
            return ifFalse.apply(state);
        }
    }

    // todo:
//    @Override
//    protected boolean isPure0() {
//        return condition.operationAreYouPureQuestionMark() && ifTrue.operationAreYouPureQuestionMark() && ifFalse.operationAreYouPureQuestionMark();
//    }
}
