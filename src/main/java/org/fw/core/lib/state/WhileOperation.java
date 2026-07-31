package org.fw.core.lib.state;

import org.fw.core.base.Val;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;
import org.fw.core.lib.BoolFw;
import org.fw.core.state.operation.OperationFw;

import static org.fw.core.FW.telephonist;

public final class WhileOperation extends Operation {
    public static final Val _While = telephonist((condition) -> {
        if (condition.type() != OperationFw.operation)
            return null;

        return telephonist((body) -> {
            if (body.type() != OperationFw.operation)
                return null;

            return new WhileOperation(condition._unpack(), body._unpack()).asVal();
        });
    });
    private final Operation condition;
    private final Operation body;

    public WhileOperation(Operation condition, Operation body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public Val execute(State state) {
        Val ret = Operation.unit;
        while (condition.execute(state) == BoolFw._true) {
            ret = body.execute(state);
        }
        return ret;
    }

    @Override
    protected boolean isPure0() {
        return condition.operationAreYouPureQuestionMark() && body.operationAreYouPureQuestionMark(); // I don't know why would you use while in this case, but still
    }
}

