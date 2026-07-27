package org.fw.core.state.operation;

import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.base.Context;
import org.fw.core.base.Val;
import org.fw.core.lib.VitFw;
import org.fw.core.lib.state.OperationFw;
import org.fw.core.state.obj.Obj;
import org.fw.core.vit.RtEnv;
import org.fw.core.vit.Vit;

import java.util.Objects;
import java.util.Set;

public final class VitOperation extends Operation {
    private final Vit vit;
    private final RtEnv rtEnv;

    VitOperation(Vit vit, RtEnv rtEnv) {
        this.vit = Objects.requireNonNull(vit);
        this.rtEnv = Objects.requireNonNull(rtEnv);
    }

    @Override
    public Val execute(Context context) {
        return vit.eval(new Context(rtEnv, context.state()));
    }

    @Override
    protected boolean isPure() {
        return vit.isPure();
    }

    public Vit vit() {
        return vit;
    }
}
