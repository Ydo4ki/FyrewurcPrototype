package org.fw.core.lib.telephonist;

import org.fw.core.FW;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.base.Call;
import org.fw.core.base.Context;
import org.fw.core.base.Type;
import org.fw.core.base.Val;

final class InstancerFw {
    public static final Type instancer = FW.telephonist("Instancer", (arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, InstancerFw.instancer, context)) {
            Val instance = Call.getVal(arg, context);
            Val cArg = Call.getArg(arg, context);

            Instancer ctor = instance._unpack();
            return Val.of(ctor.type(), cArg);
        }/* else if (arg.type().equals(ExprFw.toExpr)) {
            Val instance = BoxFw.unbox(arg);
            if (!instance.type().equals(InstancerFw.instancer))
                return Val.unspecified;

            Instancer ctor = instance._unpack();
            return ExprFw.wrap(ctor.toExpr(context));
        }*/
        return Val.unspecified;
    }).asType();

    private static final class Instancer {
        private final Type type;
        private final Val source;
        private final String name;

        private Instancer(Type type, Val source, String name) {
            this.type = type;
            this.source = source;
            this.name = name;
        }

        Type type() {
            return type;
        }

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