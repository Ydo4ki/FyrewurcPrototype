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

import java.util.Set;

public final class VitOperation extends Operation {
    private final Vit vit;
    private final RtEnv rtEnv;

    VitOperation(Vit vit, RtEnv rtEnv) {
//        if (vit instanceof VitVal val && val.val().toExpr(new Context(RtEnv.unspecified, Scope.eternal())).equals(Symbol.of("vitiate-telephonist-runtime-env")))
//            throw new Error();
        this.vit = vit;
        this.rtEnv = rtEnv;
    }

    @Override
    public Val execute(Context context) {
        return vit.eval(new Context(rtEnv, context.state()));
    }

    public Vit vit() {
        return vit;
    }

    @Override
    public Expr toExpr(Context context) {
        return ExprList.of(BracketsTypes.round,
                OperationFw.vitOperation_old.asVal().toExpr(context),
                VitFw.wrap(vit).toExpr(context)
        );
    }
}
