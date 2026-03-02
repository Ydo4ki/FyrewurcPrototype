package org.fw.lib;

import org.fw.FW;
import org.fw.FwUtils;
import org.fw.ast.BracketsTypes;
import org.fw.ast.ExprList;
import org.fw.ast.Symbol;
import org.fw.base.Call;
import org.fw.base.Context;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.lib.expr.CompEnv;
import org.fw.lib.expr.ExprCallOpFw;
import org.fw.lib.expr.ExprFw;
import org.fw.vit.Vit;

import java.math.BigInteger;

import static org.fw.FW.symbol;

public final class BoxFw {
    public static final Type boxType = FW.telephonist("BoxType", (arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, BoxFw.boxType, context)) {
            Val instance = Call.getVal(arg, context);
            Val cArg = Call.getArg(arg, context);
            return handleBoxTypeCall(instance.asType(), cArg, context);
        } else if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"), context);
            Val cEnv = arg.call(symbol("comp-env"), context);
            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 1) {
                return Val.unspecified;
            }

            Val retVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(0), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(retVit.type()))
                return retVit; // compile error idk

            return VitFw.wrap(Vit.val(BoxFw.boxType.asVal()).call(symbol("constructor")).call(VitFw.unwrap(retVit)));
        } else if (arg.equals(symbol("constructor"))) {
//            return InstancerFw.mkInstancer(BoxFw.boxType, BoxFw.boxType.asVal(), "constructor");
            return FW.telephonist(ExprList.of(BracketsTypes.round,
                    Symbol.of("get"),
                    BoxFw.boxType.asVal().toExpr(context),
                    Symbol.of("constructor")
            ), (arg1, _) -> {
                return Val.of(BoxFw.boxType, arg1);
            });
        } else if (arg.type().equals(ExprFw.toExpr)) {
            Val instance = unbox(arg);
            if (!instance.type().equals(BoxFw.boxType)) return Val.unspecified;
            return ExprFw.wrap(ExprList.of(BracketsTypes.round, BoxFw.boxType.asVal().toExpr(context), unbox(instance).toExpr(context)));
        }
        return Val.unspecified;
    }).asType();

    private static Val handleBoxTypeCall(Type type, Val arg, Context context) {
        if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"), context);
            Val cEnv = arg.call(symbol("comp-env"), context);
            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 1) {
                return Val.unspecified;
            }

            Val retVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(0), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(retVit.type()))
                return retVit; // compile error idk

            return VitFw.wrap(Vit.val(type.asVal()).call(symbol("constructor")).call(VitFw.unwrap(retVit)));
        } else if (FwUtils.isTypeApiCall(arg, type, context)) {
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
                    ), (arg1, context1) -> {
                        return Val.of(type, arg1);
                    });
        } else if (arg.type().equals(ExprFw.toExpr)) {
            Val instance = unbox(arg);
            if (!instance.type().equals(type)) return Val.unspecified;
            return ExprFw.wrap(ExprList.of(BracketsTypes.round, type.asVal().toExpr(context), unbox(instance).toExpr(context)));
        }
        return Val.unspecified;
    }

    // the only operation that doesn't need context xd
    public static Val unbox(Val arg) {
        return arg._unpack();
    }

    public static Type newBoxType(Val key, Context context) {
        return boxType.asVal().call(symbol("constructor"), context).call(key, context).asType();
    }
}
