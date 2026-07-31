package org.fw.core.lib.state;

import org.fw.core.base.context.Context;
import org.fw.core.base.Val;
import org.fw.core.state.operation.Operation;
import org.fw.core.lib.BoolFw;

public final class WhileOperation extends Operation {
    private final Operation condition;
    private final Operation body;

    public WhileOperation(Operation condition, Operation body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public Val execute(Context context) {
        Val ret = Operation.unit;
        while (condition.execute(context) == BoolFw._true) {
            ret = body.execute(context);
        }
        return ret;
    }

    @Override
    protected boolean isPure0() {
        return condition.operationAreYouPureQuestionMark() && body.operationAreYouPureQuestionMark(); // I don't know why would you use while in this case, but still
    }
}

