package org.fw.core.state.operation;

import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.base.Context;
import org.fw.core.base.Val;
import org.fw.core.lib.VitFw;
import org.fw.core.lib.state.OperationFw;
import org.fw.core.state.obj.Obj;
import org.fw.core.vit.Vit;

import java.util.Set;

public final class VitOperation implements Operation {
    private final Vit vit;
    private Set<Obj> reads;
    private Set<Obj> writes;
    private Context lastContext = null;

    VitOperation(Vit vit) {
//        if (vit instanceof VitVal val && val.val().toExpr(new Context(RtEnv.unspecified, Scope.eternal())).equals(Symbol.of("vitiate-telephonist-runtime-env")))
//            throw new Error();
        this.vit = vit;
    }

    @Override
    public Val execute(Context context) {
        return vit.eval(context);
    }

    public Vit vit() {
        return vit;
    }

    @Override
    public Set<Obj> reads(Context context) {
        if (lastContext != context || reads == null) {
            this.reads = Vit.reads(vit, context);
            lastContext = context;
        }
        return reads;
    }

    @Override
    public Set<Obj> writes(Context context) {
        if (lastContext != context || writes == null) {
            this.writes = Vit.writes(vit, context);
            lastContext = context;
        }
        return writes;
    }

    @Override
    public Expr toExpr(Context context) {
        return ExprList.of(BracketsTypes.round,
                OperationFw.vitOperation.asVal().toExpr(context),
                VitFw.wrap(vit).toExpr(context)
        );
    }
}
