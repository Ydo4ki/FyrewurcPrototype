package org.fw.std;

import org.fw.base.*;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;

import org.fw.core.constraint.Constraint;
import org.fw.core.util.FwUtils;
import org.fw.core.vit.Vit;

import java.util.WeakHashMap;

import static org.fw.core.FW.symbol;

// I'll add normal form
// when I think it up
public final class ConstraintFw {
    private static final WeakHashMap<Val, Val> typeConstraints = new WeakHashMap<>();

    public static final Val to_constraint = FW.telephonist_native("to-constraint", (arg) -> {
        if (arg.getType().equals(ConstraintFw.constraint))
            return arg;

        Value ret = (Val) arg.call(FW.symbol("to-constraint"));

        if (isConstraint(ret))
            return ret;

        return typeConstraints.computeIfAbsent(arg, arg0 -> {
            Val a = VitFw.wrap(Vit.call(EqFw.eq, Vit.call(TypeGetFw.typeGet, Vit.var)).call(arg0));
            return (Val) (Val) ConstraintFw.constraintBuilder.call(a);
        });
    });

    public static Val wrap(Constraint constraint) {
        return Val._NEW_INSTANCE_(ConstraintFw.constraint, constraint);
    }

    public static Constraint unwrap(Val constraint) {
        if (constraint.getType() == ConstraintFw.constraint) {
            return unwrap0(constraint);
        }
        return null;
    }

    public static Constraint unwrap0(Val constraint) {
        return constraint._UNPACK_(Constraint.class);
    }

    public static Val toConstraint(Val val) {
        return (Val) to_constraint.call(val);
    }

    public static Val toConstraint(Type type) {
        return (Val) to_constraint.call(type.asVal());
    }

    public static final Val constraintBuilder = FW.telephonist_native("Constraint.constructor", (arg1) -> {
        if (!VitFw.isVit(arg1.getType()))
            return null;
        return Val._NEW_INSTANCE_(ConstraintFw.constraint, Constraint.of(arg1._UNPACK_(Vit.class)));
    });

    public static final Type constraint = FW.telephonist_native("Constraint", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, ConstraintFw.constraint)) {
            Val instance = (Val) CallFw.getVal(arg);
            Val arg2 = (Val) CallFw.getArg(arg);
            if (arg2.getType().equals(SymbolFw.symbol)) {
                String val = arg2._UNPACK_().toString();
                switch (val) {
                    case "check":
                        return FW.telephonist_native("Constraint.check", (arg1) -> {

                            // we might as well do it in parallel

                            return BoolFw.wrap(instance._UNPACK_(Constraint.class).check(arg1));
                        });
    //                case "vit":
    //                    return VitFw.wrap(payload);
    //                case "b":
    //                    return VitFw.wrap(payload.b());
                }
            }
            return null;
        } else if (arg.getType().equals(SymbolFw.symbol)) {
            String value = arg._UNPACK_().toString();
            if (value.equals("construct")) {
                return constraintBuilder;
            }
        }
        return null;
    }).asType();

    public static final Val free = wrap(Constraint.free);

    public static boolean isConstraint(Value val) {
        return val.getTypeValue().impliesEquality(ConstraintFw.constraint.asVal());
    }

    public static Val constraint(Vit a) {
        return Val._NEW_INSTANCE_(ConstraintFw.constraint, Constraint.of(a));
    }

    public static final Val isSpecified = constraint(
            Vit.val(Unspecified.isUnspecified).call(Vit.var).call(symbol("not"))
    );

}
