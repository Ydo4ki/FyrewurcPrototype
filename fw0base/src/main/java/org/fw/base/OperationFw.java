package org.fw.base;

import org.fw.core.FW;

import org.fw.core.state.operation.Operation;

public final class OperationFw {

    public static final Type operation = FW.lambda_native("Operation", (arg) -> {
        return null;
    }).asType();

    public static Val wrap(Operation operation) {
        if (operation == null) return null;
        return operation.asVal();
    }

    public static Operation unwrap(Val operation) {
        if (operation.getType() == OperationFw.operation)
            return (Operation) operation._UNPACK_();
        return null;
    }
}
