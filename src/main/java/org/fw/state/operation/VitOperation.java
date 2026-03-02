package org.fw.state.operation;

import org.fw.ast.BracketsTypes;
import org.fw.ast.Expr;
import org.fw.ast.ExprList;
import org.fw.ast.Symbol;
import org.fw.base.Context;
import org.fw.base.Val;
import org.fw.lib.VitFw;
import org.fw.lib.state.OperationFw;
import org.fw.state.obj.Obj;
import org.fw.state.obj.Scope;
import org.fw.vit.RtEnv;
import org.fw.vit.Vit;
import org.fw.vit.VitVal;

import java.util.Set;

public final class VitOperation implements Operation {
    private final Vit vit;
    private Set<Obj> reads;
    private Set<Obj> writes;

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
        if (reads == null) this.reads = Vit.reads(vit, context);
        return reads;
    }

    @Override
    public Set<Obj> writes(Context context) {
        if (writes == null) this.writes = Vit.writes(vit, context);
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
