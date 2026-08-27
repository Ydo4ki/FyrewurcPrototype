package org.fw.core.state.operation;

import org.fw.core.ast.Expr;
import org.fw.core.ast.Symbol;
import org.fw.core.base.Val;
import org.fw.core.contract.InvokeContract;
import org.fw.core.state.obj.State;

public final class GetLocalStateOperation extends Operation {

    private static final GetLocalStateOperation instance = new GetLocalStateOperation();

    public static GetLocalStateOperation getInstance() {
        return instance;
    }

    private GetLocalStateOperation() {
        if (instance != null)
            throw new SecurityException();
    }

    @Override
    public Val apply(State state) {
        return state.asVal();
    }

    @Override
    public InvokeContract contract() {
        return InvokeContract.unknown(); // it reads the state object from itself
    }

    @Override
    public Expr toExpr() {
        return Symbol.of("GetLocalStateOperation");
    }
}
