package org.fw.core.lib;

import org.fw.core.FW;
import org.fw.core.FwUtils;
import org.fw.core.annotation.Insightful;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.base.Call;
import org.fw.core.base.Context;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.constraint.ConstraintFw;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.ExprCallOpFw;
import org.fw.core.lib.expr.ExprFw;
import org.fw.core.vit.Vit;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.fw.core.FW.symbol;

public final class TraitFw {

    @Insightful
    public static final Type trait = FW.telephonist("Trait", (arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, TraitFw.trait, context)) {
            Val instance = Call.getVal(arg, context);
            arg = Call.getArg(arg, context);
            Trait trait = instance._unpack();
            if (arg.equals(symbol("to-constraint"))) {
                return trait.constraint();
            }
//            if (arg.type().equals(ExprFw.toExpr)) {
//                Val strInstance = BoxFw.unbox(arg);
//                if (!strInstance.type().equals(instance.asType()))
//                    return Val.unspecified;
//
//                Val[] value = strInstance._unpack();
//                List<Expr> elements = new ArrayList<>();
//                elements.add(instance.toExpr(context));
//                for (Val val : value) {
//                    elements.add(val.toExpr(context));
//                }
//                return ExprFw.wrap(ExprList.of(BracketsTypes.round, elements));
//            }
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

            return VitFw.wrap(Vit.val(TraitFw.trait.asVal()).call(symbol("constructor")).call(VitFw.unwrap(retVit)));
        } else if (arg.equals(symbol("constructor"))) {
            return FW.telephonist("Trait.constructor", (payload, context1) -> {
                if (!payload.type().equals(DVecFw.dVec))
                    return Val.unspecified;
                Val[] fields = payload._unpack();
                for (Val field : fields) {
                    if (!field.type().equals(DeclarationFw.declaration))
                        return Val.unspecified; // some day I'll add proper errors
                }
                return Val.of(TraitFw.trait, new Trait(fields, context));
            });
        }
        return Val.unspecified;
    }).asType();

    public static Val trait(Val... fields) {
        for (Val field : fields) {
            if (!field.type().equals(DeclarationFw.declaration))
                throw new IllegalArgumentException("Declaration expected");
        }
        return Val.of(TraitFw.trait, new Trait(fields, Context.blank));
    }

    public static Val toExpr(Val arg, Context context) {
        TraitFw.Trait value = arg._unpack();
        List<Expr> finElements = new ArrayList<>();
        finElements.add(TraitFw.trait.asVal().toExpr(context));
        List<Expr> elements = new ArrayList<>();
        for (Val val : value.fields) {
            elements.add(val.toExpr(context));
        }
        finElements.add(ExprList.of(BracketsTypes.square, elements));
        return ExprFw.wrap(ExprList.of(BracketsTypes.round, finElements));
    }

    private static final class Trait {
        private final Val[] fields;
        private final Val constraint;

        private Trait(Val[] fields, Context context) {
            this.fields = fields;
            this.constraint = toConstraint(this, context);
        }

        private static Val toConstraint(Trait trait, Context context) {
            Vit a = Vit.val(BoolFw._true);
            for (Val field : trait.fields) {
                Val key = DeclarationFw.getKey(field, context);
                Val constraint = DeclarationFw.getConstraint(field, context);
                Vit fieldChecker = Vit.call(constraint, symbol("check")).call(Vit.call(Vit.var, key));
                a = a.call(symbol("and")).call(fieldChecker);
            }
            Vit b = Vit.val(BoolFw._true);
            return ConstraintFw.constraintBuilder
                    .call(VitFw.wrap(a), context)
                    .call(VitFw.wrap(b), context);
        }

        public int indexOf(Val key, Context context) {
            for (int i = 0; i < fields.length; i++) {
                Val field = fields[i];
                if (DeclarationFw.getKey(field, context).equals(key))
                    return i;
            }
            return -1;
        }

        public Val[] fields() {
            return fields;
        }

        public Val constraint() {
            return constraint;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            Trait that = (Trait) obj;
            return Arrays.equals(this.fields, that.fields);
        }

        @Override
        public int hashCode() {
            return Objects.hash((Object[]) fields);
        }

        @Override
        public String toString() {
            return "Trait[" +
                    "fields=" + Arrays.toString(fields) + ']';
        }
    }
}
