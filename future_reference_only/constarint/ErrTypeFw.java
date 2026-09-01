package org.fw.lib;

import org.fw.FW;
import org.fw.FwUtils;
import org.fw.ast.BracketsTypes;
import org.fw.ast.Expr;
import org.fw.ast.ExprList;
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

public final class ErrTypeFw {
    public static final Type errType = FW.telephonist("ErrType", (arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, ErrTypeFw.errType, context)) {
            Val instance = Call.getVal(arg, context);
            arg = Call.getArg(arg, context);
            ErrType wn = instance._unpack();
            if (FwUtils.isTypeApiCall(arg, instance.asType(), context)) {
                Val strInstance = Call.getVal(arg, context);
                arg = Call.getArg(arg, context);
                return wn.original().asType().callInstance(strInstance._unpack(), arg, context);
            } else if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
                Val ctorVal = wn.original().call(arg, context);
                if (!VitFw.isVit(ctorVal.type()))
                    return ctorVal; // compile error idk

                Vit ctor = VitFw.unwrap(ctorVal);
                assert ctor != null;

                ctor = Vit.val(instance).call(symbol("construct")).call(ctor);

                return VitFw.wrap(ctor);
            } else if (arg.equals(symbol("construct"))) {
                return FW.telephonist(instance.toExpr(context) + ".constructor", (arg1, context1) -> {
                    if (!arg1.type().equals(wn.original().asType()))
                        return Val.unspecified;
                    return Val.of(instance.asType(), arg1);
                });
                // return structBuilder(struct, instance);
            } else if (arg.type().equals(ExprFw.toExpr)) {
                Val strInstance = BoxFw.unbox(arg);
                if (!strInstance.type().equals(instance.asType()))
                    return Val.unspecified;

                Val[] value = strInstance._unpack(Val.class)._unpack();
                List<Expr> elements = new ArrayList<>();
                elements.add(instance.toExpr(context));
                for (Val val : value) {
                    elements.add(val.toExpr(context));
                }
                return ExprFw.wrap(ExprList.of(BracketsTypes.round, elements));
            }
        } else if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"), context);
            Val cEnv = arg.call(symbol("comp-env"), context);
            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 2) {
                return Val.unspecified;
            }

            Val origRetVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(0), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(origRetVit.type()))
                return origRetVit; // compile error idk

            Val nameVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(nameVit.type()))
                return nameVit; // compile error idk

            return VitFw.wrap(Vit.val(ErrTypeFw.errType.asVal()).call(symbol("builder"))
                    .call(VitFw.unwrap(origRetVit))
                    .call(VitFw.unwrap(nameVit))
            );
        } else if (arg.equals(symbol("builder"))) {
            return FW.telephonist("ErrType.builder", (originalVal, context1)
                    -> FW.telephonist(() -> "(ErrType.builder " + originalVal.toExpr(context1) + ")", (name, context2) -> {
                if (!ExprFw.isExpr(name)) {
                    if (name.type().equals(StrFw.str)) name = symbol(name._unpack());
                    else return Val.unspecified;
                }
                return Val.of(ErrTypeFw.errType, new ErrType(originalVal, name._unpack()));
            }));
        } else if (arg.type().equals(ExprFw.toExpr)) {
            Val instance = BoxFw.unbox(arg);
            if (!instance.type().equals(ErrTypeFw.errType))
                return Val.unspecified;

            ErrType value = instance._unpack();
            return ExprFw.wrap(value.name());
        }
        return Val.unspecified;
    }).asType();

    private record ErrType(Val original, Expr name) {
    }
}
