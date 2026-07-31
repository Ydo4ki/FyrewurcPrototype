package org.fw.core.lib.state;

import org.fw.core.base.Val;
import org.fw.core.lib.BoolFw;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;
import org.fw.core.state.operation.OperationFw;

import static org.fw.core.FW.telephonist;

// it's not like this can't be implemented on the language itself, this just seems easier
public final class IfOperation extends Operation {
    public static final Val _If = telephonist((condition) -> {
        if (condition.type() != OperationFw.operation)
            return null;

        return telephonist((ifTrue) -> {
            if (ifTrue.type() != OperationFw.operation)
                return null;

            return telephonist((ifFalse) -> {
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
    public Val execute(State state) {
        if (condition.execute(state) == BoolFw._true) {
            return ifTrue.execute(state);
        } else {
            return ifFalse.execute(state);
        }
    }

    @Override
    protected boolean isPure0() {
        return condition.operationAreYouPureQuestionMark() && ifTrue.operationAreYouPureQuestionMark() && ifFalse.operationAreYouPureQuestionMark();
    }
}
