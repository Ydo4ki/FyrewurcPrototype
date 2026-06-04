package org.fw.core.state.operation;

import org.fw.core.ast.Expr;
import org.fw.core.ast.Symbol;
import org.fw.core.base.Context;
import org.fw.core.base.Val;
import org.fw.core.lib.state.OperationFw;
import org.fw.core.lib.state.StateHoleFw;
import org.fw.core.state.obj.Obj;

import java.util.Collections;
import java.util.Set;

public final class LocalScopeOperation implements Operation {

    private static final LocalScopeOperation instance = new LocalScopeOperation();
    private static Val localScopeOperationInstance;

    public static LocalScopeOperation getInstance() {
        return instance;
    }

    private LocalScopeOperation() {
        if (instance != null)
            throw new SecurityException();
    }

    @Override
    public Val execute(Context context) {
        return StateHoleFw.wrap(context.scope());
    }

    @Override
    public Set<Obj> reads(Context context) {
        return Collections.singleton(context.scope()); // lmao
    }

    @Override
    public Set<Obj> writes(Context context) {
        return Collections.emptySet();
    }

    @Override
    public Expr toExpr(Context context) {
        return Symbol.of("LocalScopeOperation");
    }

    @Override
    public Val asVal() {
        if (localScopeOperationInstance == null) {
            localScopeOperationInstance = Val.of(OperationFw.localScopeOperation, LocalScopeOperation.getInstance());
        }
        return localScopeOperationInstance;
    }
}
