package org.fw.core.vit;

import org.fw.core.FyrewurcException;
import org.fw.core.ast.Expr;
import org.fw.core.base.Val;
import org.fw.core.base.context.RtEnv;
import org.fw.lib.elib.expr.ExprFw;

public class VitCompilationException extends FyrewurcException {
    private final Val value;

    public VitCompilationException(Expr value) {
        super(value.toString());
        this.value = ExprFw.wrap(value);
    }

    public VitCompilationException(Val value) {
        super(value.toString());
        this.value = value;
    }

    public VitCompilationException(Val value, Val toExpr) {
        super(value.toExpr(toExpr).toString());
        this.value = value;
    }

    public Val getValue() {
        return value;
    }
}
