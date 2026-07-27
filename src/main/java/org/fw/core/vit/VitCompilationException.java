package org.fw.core.vit;

import org.fw.core.FyrewurcException;
import org.fw.core.base.context.Context;
import org.fw.core.base.Val;

public class VitCompilationException extends FyrewurcException {
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
