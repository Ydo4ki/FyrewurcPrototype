package org.fw.core.lib;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.base.context.Context;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;

import static org.fw.core.FW.symbol;

public final class BoxFw {
    public static final Type boxType = FW.telephonist("BoxType", (arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, BoxFw.boxType, context)) {
            Val instance = Call.getVal(arg, context);
            Val cArg = Call.getArg(arg, context);
            return handleBoxTypeCall(instance.asType(), cArg, context);
        } else if (arg.equals(symbol("constructor"))) {
//            return InstancerFw.mkInstancer(BoxFw.boxType, BoxFw.boxType.asVal(), "constructor");
            return FW.telephonist(ExprList.of(BracketsTypes.round,
                    Symbol.of("get"),
                    BoxFw.boxType.asVal().toExpr(context),
                    Symbol.of("constructor")
            ), (arg1, c) -> {
                return Val.of(BoxFw.boxType, arg1);
            });
        }
        return null;
    }).asType();

    @Deprecated
    private static Val handleBoxTypeCall(Type type, Val arg, Context context) {
        if (FwUtils.isTypeApiCall(arg, type, context)) {
            Val instance = Call.getVal(arg, context);
            Val cArg = Call.getArg(arg, context);
            if (cArg.equals(symbol("unbox"))) {
                return unbox(instance);
            }
            // the hell is this then
//        } else if (arg.type().equals(BoxFw.newInstance)) {
//            Val key = BoxFw.unbox(arg);
//            return Val.of(type, key);
//        }
//            else
        } else if (arg.equals(symbol("constructor"))) {
//            return InstancerFw.mkInstancer(type, type.asVal(), "constructor");
            return FW.telephonist(
                    ExprList.of(BracketsTypes.round,
                            Symbol.of("get"),
                            type.asVal().toExpr(context),
                            Symbol.of("constructor")
                    ), (arg1, context1) -> Val.of(type, arg1));
        }
        return null;
    }

    // the only operation that doesn't need context xd
    public static Val unbox(Val arg) {
        return arg._unpack();
    }

    public static Type newBoxType(Val key, Context context) {
        return boxType.asVal().call(symbol("constructor"), context).call(key, context).asType();
    }
}
