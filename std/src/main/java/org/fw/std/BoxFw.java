package org.fw.std;

import org.fw.base.CallFw;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.core.FW;

import org.fw.core.abstrait.Value;
import org.fw.esast.expr.CompEnv;
import org.fw.esast.expr.ExprFw;
import org.fw.esast.expr.Lib;
import org.fw.esast.expr.SyntaxResolveFw;
import org.fw.core.util.FwUtils;
import com.ydo4ki.esast.BracketsTypes;
import com.ydo4ki.esast.ExprList;

import static org.fw.core.FW.symbol;

public final class BoxFw {
    public static final Type boxType = FW.telephonist_native("BoxType", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, BoxFw.boxType)) {
            Val instance = (Val) CallFw.getVal(arg);
            arg = (Val) CallFw.getArg(arg);

            Type type = instance.asType();
            if (FwUtils.isTypeApiCall(arg, type)) {
                instance = (Val) CallFw.getVal(arg);
                Val cArg = (Val) CallFw.getArg(arg);
                if (cArg.equalsSymbol("unbox")) {
                    return unbox(instance);
                }
            } else if (arg.equalsSymbol("construct")) {
                return FW.telephonist_native((arg1) -> Val._NEW_INSTANCE_(type, arg1));
            }
            return null;
        } else if (arg.equalsSymbol("construct")) {
            return FW.telephonist_native(arg1 -> Val._NEW_INSTANCE_(BoxFw.boxType, arg1));
        }
        return null;
    }).asType();

    public static final CompEnv box2exprCenv = CompEnv.of(FW.telephonist_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.toExprResolve)) {
            CompEnv compEnv = CompEnv.of((Val) arg.get("chain"));

            arg = (Val) arg.call(FW.symbol("passing"));

            Type type = arg.getType();
            if (type.equals(boxType)) {
                Value value = boxType.asVal();
                return ExprFw.wrap(ExprList.of(BracketsTypes.round, compEnv.toExpr(value), compEnv.toExpr(unbox(arg))));
            } else if (type.asVal().getType().equals(boxType)) {
                Value value = type.asVal();
                return ExprFw.wrap(ExprList.of(BracketsTypes.round, compEnv.toExpr(value), compEnv.toExpr(unbox(arg))));
            }
            return null;
        }
        return null;
    }));

    // the only operation that doesn't need context xd
    public static Val unbox(Val arg) {
        return arg._UNPACK_();
    }

    public static Type newBoxType(Val key) {
        Val val = boxType.asVal();
        Val val1 = ((Val) val.call(symbol("construct")));
        return ((Val) val1.call(key)).asType();
    }

    public static final Lib lib = Lib.of(ModuleFw.module(
                    DeclaredFw.declared(symbol("BoxType"), boxType)
            ),
            box2exprCenv.asValue()
    );
}
