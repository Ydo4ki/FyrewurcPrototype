package org.fw.lib.elib;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.lib.elib.expr.ExprFw;
import org.fw.lib.elib.expr.ToExprFn;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.ExprList;

import static org.fw.core.FW.symbol;

public final class BoxFw {
    public static final Type boxType = FW.telephonist("BoxType", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, BoxFw.boxType)) {
            Val instance = Call.getVal(arg);
            Val cArg = Call.getArg(arg);
            return handleBoxTypeCall(instance.asType(), cArg);
        } else if (arg.equals(symbol("construct"))) {
//            return InstancerFw.mkInstancer(BoxFw.boxType, BoxFw.boxType.asVal(), "constructor");
            return FW.telephonist(arg1 -> Val.of(BoxFw.boxType, arg1));
        }
        return null;
    }).asType();
    public static final Val boxToExpr = FW.telephonist((arg) -> {
        if (arg.type() != ToExprFn.toExprResolve)
            return null;
        Val toExpr = arg.call(symbol("chain"));
        arg = arg.call(symbol("passing"));

        Type type = arg.type();
        if (type.equals(boxType)) {
            return ExprFw.wrap(ExprList.of(BracketsTypes.round, boxType.asVal().toExpr(toExpr), unbox(arg).toExpr(toExpr)));
        } else if (type.asVal().type().equals(boxType)) {
            return ExprFw.wrap(ExprList.of(BracketsTypes.round, type.asVal().toExpr(toExpr), unbox(arg).toExpr(toExpr)));
        }
        return null;
    });

    @Deprecated
    private static Val handleBoxTypeCall(Type type, Val arg) {
        if (FwUtils.isTypeApiCall(arg, type)) {
            Val instance = Call.getVal(arg);
            Val cArg = Call.getArg(arg);
            if (cArg.equals(symbol("unbox"))) {
                return unbox(instance);
            }
            // the hell is this then
//        } else if (arg.type().equals(BoxFw.newInstance)) {
//            Val key = BoxFw.unbox(arg);
//            return Val.of(type, key);
//        }
//            else
        } else if (arg.equals(symbol("construct"))) {
//            return InstancerFw.mkInstancer(type, type.asVal(), "constructor");
            return FW.telephonist((arg1) -> Val.of(type, arg1));
        }
        return null;
    }

    // the only operation that doesn't need context xd
    public static Val unbox(Val arg) {
        return arg._unpack();
    }

    public static Type newBoxType(Val key) {
        return boxType.asVal().call(symbol("construct")).call(key).asType();
    }

    public static final Lib lib = Lib.ofModule(ModuleFw.module(
            DeclaredFw.declared(symbol("BoxType"), boxType)
    ));
}
