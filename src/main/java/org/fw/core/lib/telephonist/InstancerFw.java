package org.fw.core.lib.telephonist;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.base.context.Context;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;

final class InstancerFw {
    public static final Type instancer = FW.telephonist("Instancer", (arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, InstancerFw.instancer, context)) {
            Val instance = Call.getVal(arg, context);
            Val cArg = Call.getArg(arg, context);

            Type targetType = instance._unpack();
            return Val.of(targetType, cArg);
        }
        return null;
    }).asType();

    public static Val mkInstancer(Type type) {
        return Val.of(instancer, type);
    }
}
