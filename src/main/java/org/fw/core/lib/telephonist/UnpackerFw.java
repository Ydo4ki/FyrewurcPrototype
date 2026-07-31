package org.fw.core.lib.telephonist;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.base.context.Context;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;

final class UnpackerFw {
    public static final Type unpacker = FW.telephonist("Unpacker", (arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, UnpackerFw.unpacker, context)) {
            Val instance = Call.getVal(arg, context);
            Val cArg = Call.getArg(arg, context);

            Type targetType = instance._unpack();
            if (!cArg.type().equals(targetType) || !(cArg._unpack() instanceof Val)) {
                return null; // wrong unpacker / unsupported value / consider using boxes
            }
            return cArg._unpack(Val.class);
        }
        return null;
    }).asType();

    public static Val mkUnpacker(Type type) {
        return Val.of(unpacker, type);
    }
}
