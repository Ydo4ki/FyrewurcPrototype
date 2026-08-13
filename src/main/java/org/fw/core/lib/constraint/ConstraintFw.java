package org.fw.core.lib.constraint;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.lib.*;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.Symbol;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.ExprCallOpFw;
import org.fw.core.base.context.RtEnv;
import org.fw.core.vit.Vit;

import java.math.BigInteger;
import java.util.WeakHashMap;

import static org.fw.core.FW.symbol;

// I'll add normal form
// when I think it up
public final class ConstraintFw {
    private static final WeakHashMap<Val, Val> typeConstraints = new WeakHashMap<>();

    public static final Val to_constraint = FW.telephonist("to-constraint", (arg) -> {
        if (arg.type().equals(ConstraintFw.constraint))
            return arg;

        Val ret = arg.call(symbol("to-constraint"));

        if (isConstraint(ret))
            return ret;

        return typeConstraints.computeIfAbsent(arg, arg0 -> {
            Val a = VitFw.wrap(Vit.call(ValsFw.eq, Vit.call(ValsFw.typeGet, Vit.var)));
            return ConstraintFw.constraintBuilder.call(a);
        });
    });

    public static Val toConstraint(Val val) {
        return to_constraint.call(val);
    }

    public static Val toConstraint(Type type) {
        return to_constraint.call(type.asVal());
    }

    @Deprecated
    public static final Val at = FW.telephonist("@", (arg) -> {
        if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"));
            Val cEnv = arg.call(symbol("comp-env"));
            int isize = size._unpack(BigInteger.class).intValue();
            if (isize == 1) {
                Val retVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(0))._unpack(), CompEnv.of(cEnv)));
                if (!VitFw.isVit(retVit.type()))
                    return retVit; // compile error idk

                return VitFw.wrap(
                        Vit.call(to_constraint, retVit._unpack(Vit.class))
//                        Vit.call(
//                                Vit.call(ConstraintFw.constraintBuilder, VitFw.wrap(Vit.call(ValsFw.typeGet, Vit.var))),
//                                retVit
//                        )
                );
            }
        }
        return null;
    });

    public static final Val constraintBuilder = FW.telephonist("Constraint.constructor", (arg1) -> {
        if (!VitFw.isVit(arg1.type()))
            return null;
        return Val.of(ConstraintFw.constraint, arg1);
    });

    public static final Type constraint = FW.telephonist("Constraint", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, ConstraintFw.constraint)) {
            Val instance = Call.getVal(arg);
            return handleInstanceCall(instance, instance._unpack(), Call.getArg(arg));
        } else if (arg.type().equals(SymbolFw.symbol)) {
            String value = arg._unpack(Symbol.class).getValue();
            if (value.equals("constructor")) {
                return constraintBuilder;
            }
        }
        return null;
    }).asType();

    private static Val handleInstanceCall(Val instance, Vit payload, Val arg) {
        if (arg.type().equals(SymbolFw.symbol)) {
            String val = arg._unpack(Symbol.class).getValue();
            switch (val) {
                case "check":
                    return FW.telephonist("Constraint.check", (arg1) -> {
                        RtEnv rtEnv = RtEnv.of(arg1);

                        // we might as well do it in parallel

                        return BoolFw.wrap(payload.eval(rtEnv) == BoolFw._true);
                    });
//                case "a":
//                    return VitFw.wrap(payload.a());
//                case "b":
//                    return VitFw.wrap(payload.b());
            }
        }
        return null;
    }

    public static final Val free = constraint(Vit.val(BoolFw._true));

    public static boolean isConstraint(Val val) {
        return val.type().equals(ConstraintFw.constraint);
    }

    public static Val constraint(Vit a) {
        return Val.of(ConstraintFw.constraint, a);
    }

    public static final Val isSpecified = constraint(
            Vit.val(ValsFw.isUnspecified).call(Vit.var).call(symbol("not"))
    );
}
