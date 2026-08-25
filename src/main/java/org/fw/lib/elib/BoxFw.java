package org.fw.lib.elib;

import org.fw.core.FW;
import org.fw.core.ast.Symbol;
import org.fw.core.base.*;
import org.fw.lib.elib.expr.CompEnv;
import org.fw.lib.elib.expr.ExprFw;
import org.fw.lib.elib.expr.SyntaxResolveFw;
import org.fw.lib.elib.expr.ToExprFn;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.ExprList;

import static org.fw.core.FW.symbol;

public final class BoxFw {
    public static final Type boxType = FW.telephonist("BoxType", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, BoxFw.boxType)) {
            Val instance = Call.getVal(arg);
            arg = Call.getArg(arg);

            Type type = instance.asType();
            if (FwUtils.isTypeApiCall(arg, type)) {
                instance = Call.getVal(arg);
                Val cArg = Call.getArg(arg);
                if (cArg.equals(symbol("unbox"))) {
                    return unbox(instance);
                }
            } else if (arg.equals(symbol("construct"))) {
                return FW.telephonist((arg1) -> Val.of(type, arg1));
            }
            return null;
        } else if (arg.equals(symbol("construct"))) {
            return FW.telephonist(arg1 -> Val.of(BoxFw.boxType, arg1));
        }
        return null;
    }).asType();

    public static final CompEnv box2exprCenv = CompEnv.of(FW.telephonist((arg) -> {
        if (arg.type().equals(SyntaxResolveFw.toExprResolve)) {
            CompEnv compEnv = CompEnv.of(arg.get("chain"));

            arg = arg.call(symbol("passing"));

            Type type = arg.type();
            if (type.equals(boxType)) {
                return ExprFw.wrap(ExprList.of(BracketsTypes.round, boxType.asVal().toExpr(compEnv), unbox(arg).toExpr(compEnv)));
            } else if (type.asVal().type().equals(boxType)) {
                return ExprFw.wrap(ExprList.of(BracketsTypes.round, type.asVal().toExpr(compEnv), unbox(arg).toExpr(compEnv)));
            }
            return null;
        }
        return null;
    }));

    // the only operation that doesn't need context xd
    public static Val unbox(Val arg) {
        return arg._unpack();
    }

    public static Type newBoxType(Val key) {
        return boxType.asVal().call(symbol("construct")).call(key).asType();
    }

    public static final Lib lib = Lib.of(ModuleFw.module(
                    DeclaredFw.declared(symbol("BoxType"), boxType)
            ),
            box2exprCenv.asVal()
    );
}
