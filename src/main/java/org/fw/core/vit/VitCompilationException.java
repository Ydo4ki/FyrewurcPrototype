package org.fw.core.vit;

import org.fw.core.base.Context;
import org.fw.core.base.Val;

public class VitCompilationException extends Exception {
    private final Val value;

    public VitCompilationException(Val value) {
        super(value.toString());
        this.value = value;
    }

    public VitCompilationException(Val value, Context context) {
        super(value.toExpr(context).toString());
        this.value = value;
    }

    public Val getValue() {
        return value;
    }
}
