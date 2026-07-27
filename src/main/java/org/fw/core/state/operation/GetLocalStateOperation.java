package org.fw.core.state.operation;

import org.fw.core.ast.Expr;
import org.fw.core.ast.Symbol;
import org.fw.core.base.context.Context;
import org.fw.core.base.Val;

// but I have no idea how would you use it
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
    public Val execute(Context context) {
        return context.state().asVal();
    }

    @Override
    public Expr toExpr(Context context) {
        return Symbol.of("GetLocalStateOperation");
    }

    @Override
    protected boolean isPure0() {
        return false;
    }
}
