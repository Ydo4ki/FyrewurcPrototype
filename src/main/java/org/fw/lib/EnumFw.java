package org.fw.lib;

import org.fw.FW;
import org.fw.FwUtils;
import org.fw.ast.BracketsTypes;
import org.fw.ast.Expr;
import org.fw.ast.ExprList;
import org.fw.ast.Symbol;
import org.fw.base.Call;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.lib.expr.CompEnv;
import org.fw.lib.expr.ExprCallOpFw;
import org.fw.lib.expr.ExprFw;
import org.fw.vit.Vit;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.fw.FW.symbol;

public final class EnumFw {
    public static final Type enumeration = FW.telephonist("Enum", (arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, EnumFw.enumeration, context)) {
            Val instance = Call.getVal(arg, context);
            arg = Call.getArg(arg, context);
            Enum anEnum = instance._unpack();
            for (Val value : anEnum.values) {
                if (value._unpack(Val.class).equals(arg)) return value;
            }
            if (arg.type().equals(ExprFw.toExpr)) {
                Val instanceEnum = BoxFw.unbox(arg);
                if (!instanceEnum.type().equals(instance.asType()))
                    return Val.unspecified;

                Val value = instanceEnum._unpack();
                return value; // it supposes to be a symbol
            }
            return Val.unspecified;
        }
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

            return VitFw.wrap(Vit.val(EnumFw.enumeration.asVal()).call(symbol("constructor")).call(VitFw.unwrap(retVit)));
        } else if (arg.equals(symbol("constructor"))) {
            return FW.telephonist("Enum.constructor", (payload, context1) -> {
                if (!payload.type().equals(DVecFw.dVec))
                    return Val.unspecified;
                Val[] keys = payload._unpack();
                Val[] values = new Val[keys.length];
                Type resultingType = Val.of(EnumFw.enumeration, new Enum(values)).asType();
                for (int i = 0; i < keys.length; i++) {
                    if (!keys[i].type().equals(ExprFw.symbol))
                        return Val.unspecified;

                    values[i] = Val.of(resultingType, keys[i]);
                }
                return resultingType.asVal();
            });
        } else if (arg.type().equals(ExprFw.toExpr)) {
            Val instance = BoxFw.unbox(arg);
            if (!instance.type().equals(EnumFw.enumeration))
                return Val.unspecified;

            Enum value = instance._unpack();
            List<Expr> finElements = new ArrayList<>();
            finElements.add(EnumFw.enumeration.asVal().toExpr(context));
            List<Expr> elements = new ArrayList<>();
            for (Val val : value.values()) {
                elements.add(val._unpack(Val.class).toExpr(context));
            }
            finElements.add(ExprList.of(BracketsTypes.square, elements));
            return ExprFw.wrap(ExprList.of(BracketsTypes.round, finElements));
        }
        return Val.unspecified;
    }).asType();

    private record Enum(Val[] values) {
    }
}
