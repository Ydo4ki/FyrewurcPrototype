package org.fw.core.state.operation;

import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.base.Context;
import org.fw.core.base.Val;
import org.fw.core.lib.state.OperationFw;
import org.fw.core.lib.state.StateHoleFw;
import org.fw.core.state.obj.Obj;

import java.util.Collections;
import java.util.Set;

public final class WriteOperation extends Operation {
    private final Obj.ValObj obj;
    private final Operation x;

    WriteOperation(Obj.ValObj obj, Operation x) {
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
    public Expr toExpr(Context context) {
        return ExprList.of(BracketsTypes.round,
                OperationFw.writeOperation_old.asVal().toExpr(context),
                StateHoleFw.wrap(obj).toExpr(context),
                x.toExpr(context)
        );
    }
}
