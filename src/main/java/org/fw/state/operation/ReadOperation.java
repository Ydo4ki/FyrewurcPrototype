package org.fw.state.operation;

import org.fw.ast.BracketsTypes;
import org.fw.ast.Expr;
import org.fw.ast.ExprList;
import org.fw.base.Context;
import org.fw.base.Val;
import org.fw.lib.state.OperationFw;
import org.fw.lib.state.StateHoleFw;
import org.fw.state.obj.Obj;

import java.util.Set;

public final class ReadOperation implements Operation {
    private final Obj obj;

    ReadOperation(Obj obj) {
        this.obj = obj;
    }

    public Obj obj() {
        return obj;
    }

    @Override
    public Val execute(Context context) {
        return obj.read(context);
    }

    @Override
    public Set<Obj> reads(Context context) {
        return Set.of(obj);
    }

    @Override
    public Set<Obj> writes(Context context) {
        return Set.of();
    }

    @Override
    public Expr toExpr(Context context) {
        return ExprList.of(BracketsTypes.round,
                OperationFw.readOperation.asVal().toExpr(context),
                StateHoleFw.wrap(obj).toExpr(context)
        );
    }
}
