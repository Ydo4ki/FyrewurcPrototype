package org.fw.core.state.operation;

import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.base.Context;
import org.fw.core.base.Val;
import org.fw.core.lib.state.OperationFw;
import org.fw.core.lib.state.StateHoleFw;
import org.fw.core.state.obj.Obj;

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

    @Override
    public Val asVal() {
        return Val.of(OperationFw.writeOperation, this);
    }
}
