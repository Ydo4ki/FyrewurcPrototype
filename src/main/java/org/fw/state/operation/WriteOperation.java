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

public final class WriteOperation implements Operation {
    private final Obj obj;
    private final Operation x;

    WriteOperation(Obj obj, Operation x) {
        this.obj = obj;
        this.x = x;
    }

    public Operation x() {
        return x;
    }

    public Obj obj() {
        return obj;
    }

    @Override
    public Val execute(Context context) {
        obj.write(context, x.execute(context));
        return Operation.unit;
    }

    @Override
    public Set<Obj> reads(Context context) {
        return Set.of();
    }

    @Override
    public Set<Obj> writes(Context context) {
        return Set.of(obj);
    }

    @Override
    public Expr toExpr(Context context) {
        return ExprList.of(BracketsTypes.round,
                OperationFw.writeOperation.asVal().toExpr(context),
                StateHoleFw.wrap(obj).toExpr(context),
                x.toExpr(context)
        );
    }
}
