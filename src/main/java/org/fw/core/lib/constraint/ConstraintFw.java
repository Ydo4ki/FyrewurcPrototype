package org.fw.core.lib.constraint;

import org.fw.core.FW;
import org.fw.core.lib.*;
import org.fw.core.util.FwUtils;
import org.fw.core.annotation.Insightful;
import org.fw.core.ast.Symbol;
import org.fw.core.base.Call;
import org.fw.core.base.Context;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.ExprCallOpFw;
import org.fw.core.lib.expr.ExprFw;
import org.fw.core.state.obj.State;
import org.fw.core.vit.RtEnv;
import org.fw.core.vit.Vit;

import java.math.BigInteger;
import java.util.Objects;
import java.util.WeakHashMap;

import static org.fw.core.FW.symbol;

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

    public static Val toConstraint(Val val) {
        return to_constraint.call(val, Context.outOf);
    }

    public static Val toConstraint(Type type) {
        return to_constraint.call(type.asVal(), Context.outOf);
    }

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
                        Vit.call(to_constraint, retVit._unpack(Vit.class))
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
        return FW.telephonist((arg2, context2) -> {
            if (!VitFw.isVit(arg2.type()))
                return Val.unspecified;
            return Val.of(ConstraintFw.constraint, new Constraint(arg1._unpack(), arg2._unpack(), arg1.equals(arg2)));
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
                        Val a = State.performAndDie(scope -> {
                            return payload.a().eval(new Context(rtEnv, scope));
                        });
                        Val b = State.performAndDie(scope -> {
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

    private static final class Constraint {
        private final Vit a;
        private final Vit b;
        private final boolean alwaysTrue;

        private Constraint(Vit a, Vit b, boolean alwaysTrue) {
            this.a = a;
            this.b = b;
            this.alwaysTrue = alwaysTrue;
        }

        public Vit a() {
            return a;
        }

        public Vit b() {
            return b;
        }

        public boolean alwaysTrue() {
            return alwaysTrue;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            Constraint that = (Constraint) obj;
            return Objects.equals(this.a, that.a) &&
                    Objects.equals(this.b, that.b) &&
                    this.alwaysTrue == that.alwaysTrue;
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b, alwaysTrue);
        }

        @Override
        public String toString() {
            return "Constraint[" +
                    "a=" + a + ", " +
                    "b=" + b + ", " +
                    "alwaysTrue=" + alwaysTrue + ']';
        }
    }

    public static final Val free = constraint(Vit.var, Vit.var);

    public static boolean isConstraint(Val val) {
        return val.type().equals(ConstraintFw.constraint);
    }

    public static Val constraint(Vit a, Vit b) {
        return Val.of(ConstraintFw.constraint, new Constraint(a, b, a.equals(b)));
    }

    public static CompEnv exports = CompEnv.of(CompEnv.compEnv(Context.outOf,
            ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                    DeclaredFw.declared(symbol("Constraint"), constraint.asVal())
            ))
    ));
}
