package org.fw.lib.stdlib.state;

import org.fw.core.FW;
import org.fw.core.base.Val;
import org.fw.core.contract.InvokeContract;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;
import org.fw.core.base.BoolFw;
import org.fw.core.state.operation.OperationFw;

import static org.fw.core.FW.telephonist;

public final class WhileOperation extends Operation {
    public static final Val _While = FW.telephonist((condition) -> {
        if (condition.type() != OperationFw.operation)
            return null;

        return FW.telephonist((body) -> {
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
    public Val apply(State state) {
        Val ret = Operation.unit;
        while (condition.apply(state) == BoolFw._true) {
            ret = body.apply(state);
        }
        return ret;
    }

    @Override
    public InvokeContract contract() {
        return InvokeContract.unknown();
    }

    // todo:
//    @Override
//    protected boolean isPure0() {
//        return condition.operationAreYouPureQuestionMark() && body.operationAreYouPureQuestionMark(); // I don't know why would you use while in this case, but still
//    }
}

