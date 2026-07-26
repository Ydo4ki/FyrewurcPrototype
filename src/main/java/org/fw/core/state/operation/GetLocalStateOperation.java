package org.fw.core.state.operation;

import org.fw.core.ast.Expr;
import org.fw.core.ast.Symbol;
import org.fw.core.base.Context;
import org.fw.core.base.Val;
import org.fw.core.lib.state.StateHoleFw;
import org.fw.core.state.obj.Obj;

import java.util.Collections;
import java.util.Set;

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
        return StateHoleFw.wrap(context.scope());
    }

    @Override
    public Expr toExpr(Context context) {
        return Symbol.of("GetLocalStateOperation");
    }
}
