package org.fw.lib;

import org.fw.FW;
import org.fw.FwUtils;
import org.fw.ast.BracketsTypes;
import org.fw.ast.Expr;
import org.fw.ast.ExprList;
import org.fw.ast.Symbol;
import org.fw.base.Call;
import org.fw.base.Context;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.lib.expr.ExprFw;

final class InstancerFw {
    public static final Type instancer = FW.telephonist("Instancer", (arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, InstancerFw.instancer, context)) {
            Val instance = Call.getVal(arg, context);
            Val cArg = Call.getArg(arg, context);

            Instancer ctor = instance._unpack();
            return Val.of(ctor.type(), cArg);
        } else if (arg.type().equals(ExprFw.toExpr)) {
            Val instance = BoxFw.unbox(arg);
            if (!instance.type().equals(InstancerFw.instancer))
                return Val.unspecified;

            Instancer ctor = instance._unpack();
            return ExprFw.wrap(ctor.toExpr(context));
        }
        return Val.unspecified;
    }).asType();

    private record Instancer(Type type, Val source, String name) {
        Expr toExpr(Context context) {
            return ExprList.of(BracketsTypes.round,
                    Symbol.of("get"),
                    source.toExpr(context),
                    Symbol.of(name)
            );
        }
    }

    public static Val mkInstancer(Type type, Val source, String name) {
        return Val.of(instancer, new Instancer(type, source, name));
    }
}
