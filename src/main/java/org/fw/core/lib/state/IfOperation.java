package org.fw.core.lib.state;

import org.fw.core.base.Val;
import org.fw.core.base.context.Context;
import org.fw.core.lib.BoolFw;
import org.fw.core.state.operation.Operation;

// it's not like this can't be implemented on the language itself, this just seems easier
public final class IfOperation extends Operation {
    private final Operation condition;
    private final Operation ifTrue;
    private final Operation ifFalse;

    public IfOperation(Operation condition, Operation ifTrue, Operation ifFalse) {
        this.condition = condition;
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
    }

    @Override
    public Val execute(Context context) {
        if (condition.execute(context) == BoolFw._true) {
            return ifTrue.execute(context);
        } else {
            return ifFalse.execute(context);
        }
    }

    @Override
    protected boolean isPure0() {
        return condition.operationAreYouPureQuestionMark() && ifTrue.operationAreYouPureQuestionMark() && ifFalse.operationAreYouPureQuestionMark();
    }
}
