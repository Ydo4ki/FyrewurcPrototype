package org.fw.core.vit;

import org.fw.core.FyrewurcException;
import org.fw.core.ast.Expr;
import org.fw.core.base.Val;
import org.fw.lib.stdlib.expr.CompEnv;
import org.fw.lib.stdlib.expr.ExprFw;

public class VitCompilationException extends FyrewurcException {
    private final Val value;
    private final String string;

    public VitCompilationException(Expr value) {
        super(value.toString());
        this.value = ExprFw.wrap(value);
        this.string = null;
    }

    public VitCompilationException(Expr value, String message) {
        super(message + ": " + value.toString());
        this.value = ExprFw.wrap(value);
        this.string = message;
    }

    public VitCompilationException(Val value) {
        super(value.toString());
        this.value = value;
        this.string = null;
    }

    public VitCompilationException(Val value, CompEnv toExpr) {
        super(value.toExpr(toExpr).toString());
        this.value = value;
        this.string = null;
    }

    public Val getValue() {
        return value;
    }

    public String getString() {
        return string;
    }
}
