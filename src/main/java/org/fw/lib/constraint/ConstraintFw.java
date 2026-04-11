package org.fw.lib.constraint;

import org.fw.FW;
import org.fw.FwUtils;
import org.fw.annotation.Insightful;
import org.fw.ast.Symbol;
import org.fw.base.Call;
import org.fw.base.Context;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.lib.BoolFw;
import org.fw.lib.DIntFw;
import org.fw.lib.ValsFw;
import org.fw.lib.VitFw;
import org.fw.lib.expr.CompEnv;
import org.fw.lib.expr.ExprCallOpFw;
import org.fw.lib.expr.ExprFw;
import org.fw.state.obj.Scope;
import org.fw.vit.RtEnv;
import org.fw.vit.Vit;

import java.math.BigInteger;
import java.util.WeakHashMap;

import static org.fw.FW.symbol;

public final class ConstraintFw {

    private static final WeakHashMap<Val, Val> typeConstraints = new WeakHashMap<>();

    @Insightful
    public static final Val to_constraint = FW.telephonist("to-constraint", (arg, context) -> {
        if (arg.type().equals(ConstraintFw.constraint))
            return arg;

        Val ret = arg.call(symbol("to-constraint"), context);

        if (isConstraint(ret))
            return ret;

        return typeConstraints.computeIfAbsent(arg, arg0 -> {
            Val a = VitFw.wrap(Vit.call(ValsFw.typeGet, Vit.var));
            Val b = VitFw.wrap(Vit.val(arg0));
            return ConstraintFw.constraintBuilder.call(a, context).call(b, context);
        });
    });

    @Insightful
    public static final Val at = FW.telephonist("@", (arg, context) -> {
        if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"), context);
            Val cEnv = arg.call(symbol("comp-env"), context);
            int isize = size._unpack(BigInteger.class).intValue();
            if (isize == 1) {
                Val retVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(0), context)._unpack(), CompEnv.of(cEnv)), context);
                if (!VitFw.isVit(retVit.type()))
                    return retVit; // compile error idk

                return VitFw.wrap(
                        Vit.call(to_constraint, VitFw.unwrap(retVit))
//                        Vit.call(
//                                Vit.call(ConstraintFw.constraintBuilder, VitFw.wrap(Vit.call(ValsFw.typeGet, Vit.var))),
//                                retVit
//                        )
                );
            }
        }
        return Val.unspecified;
    });

    public static final Val constraintBuilder = FW.telephonist("Constraint.builder", (arg1, context1) -> {
        if (!VitFw.isVit(arg1.type()))
            return Val.unspecified;
        return FW.telephonist("Constraint.builderb", (arg2, context2) -> {
            if (!VitFw.isVit(arg2.type()))
                return Val.unspecified;
            return Val.of(ConstraintFw.constraint, new Constraint(VitFw.unwrap(arg1), VitFw.unwrap(arg2), arg1.equals(arg2)));
        });
    });

    @Insightful
    public static final Type constraint = FW.telephonist("Constraint", (arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, ConstraintFw.constraint, context)) {
            Val instance = Call.getVal(arg, context);
            return handleInstanceCall(instance, instance._unpack(), Call.getArg(arg, context), context);
        } else if (arg.type().equals(ExprFw.symbol)) {
            String value = arg._unpack(Symbol.class).getValue();
            if (value.equals("builder")) {
                return constraintBuilder;
            }
        }
        return Val.unspecified(ConstraintFw.constraint.asVal(), arg);
    }).asType();

    private static Val handleInstanceCall(Val instance, Constraint payload, Val arg, Context context) {
        if (arg.type().equals(ExprFw.symbol)) {
            String val = arg._unpack(Symbol.class).getValue();
            switch (val) {
                case "check":
                    return FW.telephonist("Constraint.check", (arg1, context1) -> {
                        if (payload.alwaysTrue())
                            return BoolFw._true;

                        RtEnv rtEnv = RtEnv.of(arg1);

                        // we might as well do it in parallel
                        Val a = Scope.performAndDie(context1.scope(), scope -> {
                            return payload.a().eval(new Context(rtEnv, scope));
                        });
                        Val b = Scope.performAndDie(context1.scope(), scope -> {
                            return payload.b().eval(new Context(rtEnv, scope));
                        });

                        return BoolFw.wrap(a.equals(b));
                    });
                case "a":
                    return VitFw.wrap(payload.a());
                case "b":
                    return VitFw.wrap(payload.b());
            }
        }
        return Val.unspecified(instance, arg);
    }

    private record Constraint(Vit a, Vit b, boolean alwaysTrue) {

    }

    public static boolean isConstraint(Val val) {
        return val.type().equals(ConstraintFw.constraint);
    }
}
