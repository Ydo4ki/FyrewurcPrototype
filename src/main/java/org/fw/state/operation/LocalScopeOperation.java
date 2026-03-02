package org.fw.state.operation;

import org.fw.ast.Expr;
import org.fw.ast.Symbol;
import org.fw.base.Context;
import org.fw.base.Val;
import org.fw.lib.state.StateHoleFw;
import org.fw.state.obj.Obj;

import java.util.Set;

public final class LocalScopeOperation implements Operation {

    private static final LocalScopeOperation instance = new LocalScopeOperation();

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
        return Set.of(context.scope()); // lmao
    }

    @Override
    public Set<Obj> writes(Context context) {
        return Set.of();
    }

    @Override
    public Expr toExpr(Context context) {
        return Symbol.of("LocalScopeOperation");
    }
}
