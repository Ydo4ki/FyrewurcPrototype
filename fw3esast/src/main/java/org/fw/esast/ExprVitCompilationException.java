package org.fw.esast;

import org.fw.core.FyrewurcException;
import org.fw.core.abstrait.Value;
import org.fw.base.Val;
import org.fw.esast.expr.CompEnv;
import org.fw.esast.expr.ExprFw;
import com.ydo4ki.esast.Expr;

public class ExprVitCompilationException extends FyrewurcException {
    private final Val value;
    private final String string;

    public ExprVitCompilationException(Expr value) {
        super(value.toString());
        this.value = ExprFw.wrap(value);
        this.string = null;
    }

    public ExprVitCompilationException(Expr value, String message) {
        super(message + ": " + value.toString());
        this.value = ExprFw.wrap(value);
        this.string = message;
    }

    public ExprVitCompilationException(Val value) {
        super(value.toString());
        this.value = value;
        this.string = null;
    }

    public ExprVitCompilationException(Val value, CompEnv toExpr) {
        super(toExpr.toExpr(value).toString());
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
