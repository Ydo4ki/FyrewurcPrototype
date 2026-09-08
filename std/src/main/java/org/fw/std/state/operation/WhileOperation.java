package org.fw.std.state.operation;

import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import org.fw.base.Val;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;
import org.fw.base.BoolFw;
import org.fw.base.OperationFw;

import static org.fw.core.FW.lambda_native;

public final class WhileOperation extends Operation {
    public static final Val _While = FW.lambda_native((condition) -> {
        if (condition.getType() != OperationFw.operation)
            return null;

        return FW.lambda_native((body) -> {
            if (body.getType() != OperationFw.operation)
                return null;

            return new WhileOperation(condition._UNPACK_(), body._UNPACK_()).asVal();
        });
    });
    private final Operation condition;
    private final Operation body;

    public WhileOperation(Operation condition, Operation body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public Value apply(State state) {
        Value ret = Operation.unit;
        while (condition.apply(state).impliesEquality(BoolFw._true)) {
            ret = body.apply(state);
        }
        return ret;
    }

    // todo:
//    @Override
//    protected boolean isPure0() {
//        return condition.operationAreYouPureQuestionMark() && body.operationAreYouPureQuestionMark(); // I don't know why would you use while in this case, but still
//    }
}

