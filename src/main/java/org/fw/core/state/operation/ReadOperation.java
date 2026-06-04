package org.fw.core.state.operation;

import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.base.Context;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.state.OperationFw;
import org.fw.core.lib.state.StateHoleFw;
import org.fw.core.state.obj.Obj;

import java.util.Collections;
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
        return Collections.singleton(obj);
    }

    @Override
    public Set<Obj> writes(Context context) {
        return Collections.emptySet();
    }

    @Override
    public Expr toExpr(Context context) {
        return ExprList.of(BracketsTypes.round,
                OperationFw.readOperation.asVal().toExpr(context),
                StateHoleFw.wrap(obj).toExpr(context)
        );
    }

    @Override
    public Val asVal() {
        return Val.of(OperationFw.readOperation, this);
    }
}
