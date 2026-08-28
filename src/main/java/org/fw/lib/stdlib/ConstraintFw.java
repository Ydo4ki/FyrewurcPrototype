package org.fw.lib.stdlib;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.base.contract.Constraint;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.Symbol;
import org.fw.core.base.context.RtEnv;
import org.fw.core.vit.Vit;

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
            Val a = VitFw.wrap(Vit.call(EqFw.eq, Vit.call(TypeGetFw.typeGet, Vit.var)).call(arg0));
            return ConstraintFw.constraintBuilder.call(a);
        });
    });

    public static Val wrap(Constraint constraint) {
        return Val.of(ConstraintFw.constraint, constraint);
    }

    public static Constraint unwrap(Val constraint) {
        if (constraint.type() == ConstraintFw.constraint) {
            return unwrap0(constraint);
        }
        return null;
    }

    public static Constraint unwrap0(Val constraint) {
        return constraint._unpack(Constraint.class);
    }

    public static Val toConstraint(Val val) {
        return to_constraint.call(val);
    }

    public static Val toConstraint(Type type) {
        return to_constraint.call(type.asVal());
    }

    public static final Val constraintBuilder = FW.telephonist("Constraint.constructor", (arg1) -> {
        if (!VitFw.isVit(arg1.type()))
            return null;
        return Val.of(ConstraintFw.constraint, Constraint.of(arg1._unpack(Vit.class)));
    });

    public static final Type constraint = FW.telephonist("Constraint", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, ConstraintFw.constraint)) {
            Val instance = CallFw.getVal(arg);
            return handleInstanceCall(instance, instance._unpack(), CallFw.getArg(arg));
        } else if (arg.type().equals(SymbolFw.symbol)) {
            String value = arg._unpack(Symbol.class).getValue();
            if (value.equals("constructor")) {
                return constraintBuilder;
            }
        }
        return null;
    }).asType();

    private static Val handleInstanceCall(Val instance, Constraint payload, Val arg) {
        if (arg.type().equals(SymbolFw.symbol)) {
            String val = arg._unpack(Symbol.class).getValue();
            switch (val) {
                case "check":
                    return FW.telephonist("Constraint.check", (arg1) -> {
                        RtEnv rtEnv = RtEnv.of(arg1);

                        // we might as well do it in parallel

                        return BoolFw.wrap(payload.check(rtEnv.asVal()));
                    });
//                case "vit":
//                    return VitFw.wrap(payload);
//                case "b":
//                    return VitFw.wrap(payload.b());
            }
        }
        return null;
    }

    public static final Val free = wrap(Constraint.free);

    public static boolean isConstraint(Val val) {
        return val.type().equals(ConstraintFw.constraint);
    }

    public static Val constraint(Vit a) {
        return Val.of(ConstraintFw.constraint, Constraint.of(a));
    }

    public static final Val isSpecified = constraint(
            Vit.val(Unspecified.isUnspecified).call(Vit.var).call(symbol("not"))
    );
}
