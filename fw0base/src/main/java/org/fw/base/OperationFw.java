package org.fw.base;

import org.fw.core.FW;

import org.fw.core.state.operation.Operation;

public final class OperationFw {
    private static final MagicPowers magic = MagicPowers.getMagicPowers();

    public static final Type operation = FW.telephonist_native("Operation", (c) -> {
        return null;
    }).asType();

    public static Val wrap(Operation operation) {
        if (operation == null) return null;
        return operation.asVal();
    }

    public static Operation unwrap(Val operation) {
        if (operation.getType() == OperationFw.operation)
            return (Operation) magic.unpackVal(operation);
        return null;
    }
}
