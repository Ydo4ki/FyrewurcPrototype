package org.fw;

import com.ydo4ki.esast.Expr;
import org.fw.core.FyrewurcException;

public class DirectVitCompilationException extends FyrewurcException {
    private final Expr value;
    private final String string;

    public DirectVitCompilationException(Expr value) {
        super(value.toString());
        this.value = value;
        this.string = null;
    }

    public DirectVitCompilationException(Expr value, String message) {
        super(message + ": " + value.toString());
        this.value = value;
        this.string = message;
    }
//
//    public DirectVitCompilationException(Val value) {
//        super(value.toString());
//        this.value = value;
//        this.string = null;
//    }
//
//    public DirectVitCompilationException(Val value, CompEnv toExpr) {
//        super(toExpr.toExpr((Value) value).toString());
//        this.value = value;
//        this.string = null;
//    }

    public Expr getValue() {
        return value;
    }

    public String getString() {
        return string;
    }
}
