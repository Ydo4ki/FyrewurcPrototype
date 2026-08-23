package org.fw.lib.elib.constraint;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.Symbol;
import org.fw.lib.elib.DIntFw;
import org.fw.lib.elib.VitFw;
import org.fw.lib.elib.expr.CompEnv;
import org.fw.lib.elib.expr.ExprCallOpFw;
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
            Val a = VitFw.wrap(Vit.call(ValsFw.eq, Vit.call(ValsFw.typeGet, Vit.var)).call(arg0));
            return ConstraintFw.constraintBuilder.call(a);
        });
    });

    public static Val toConstraint(Val val) {
        return to_constraint.call(val);
    }

    public static Val toConstraint(Type type) {
        return to_constraint.call(type.asVal());
    }

    public static final Val constraintBuilder = FW.telephonist("Constraint.constructor", (arg1) -> {
        if (!VitFw.isVit(arg1.type()))
            return null;
        return Val.of(ConstraintFw.constraint, VitFw.unwrap(arg1));
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
                case "vit":
                    return VitFw.wrap(payload);
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
