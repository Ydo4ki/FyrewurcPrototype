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

public final class ReadOperation extends Operation {
    private final Obj.ValObj obj;

    ReadOperation(Obj.ValObj obj) {
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
    public Expr toExpr(Context context) {
        return ExprList.of(BracketsTypes.round,
                OperationFw.readOperation_old.asVal().toExpr(context),
                StateHoleFw.wrap(obj).toExpr(context)
        );
    }
}
