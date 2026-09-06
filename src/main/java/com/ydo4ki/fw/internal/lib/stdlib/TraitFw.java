package com.ydo4ki.fw.internal.lib.stdlib;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.lib.stdlib.ConstraintFw;
import org.fw.lib.stdlib.DeclarationFw;
import org.fw.lib.stdlib.VitFw;
import org.fw.lib.stdlib.dvec.DVecFw;
import org.fw.lib.stdlib.expr.CompEnv;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.lib.stdlib.expr.ExprFw;
import org.fw.core.vit.Vit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.fw.core.FW.symbol;

final class TraitFw {

    public static final Type trait = FW.telephonist("Trait", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, TraitFw.trait)) {
            Val instance = CallFw.getVal(arg);
            arg = CallFw.getArg(arg);
            Trait trait = instance._unpack();
            if (arg.equalsSymbol("to-constraint")) {
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
        if (arg.equalsSymbol("construct")) {
            return FW.telephonist("Trait.constructor", (payload) -> {
                if (!payload.getType().equals(DVecFw.dVec))
                    return null;
                Val[] fields = payload._unpack();
                for (Val field : fields) {
                    if (!field.getType().equals(DeclarationFw.declaration))
                        return null; // some day I'll add proper errors
                }
                return Val.of(TraitFw.trait, new Trait(fields));
            });
        }
        return null;
    }).asType();

    public static Val trait(Val... fields) {
        for (Val field : fields) {
            if (!field.getType().equals(DeclarationFw.declaration))
                throw new IllegalArgumentException("Declaration expected");
        }
        return Val.of(TraitFw.trait, new Trait(fields));
    }

    public static Val toExpr(Val arg, CompEnv toExpr) {
        TraitFw.Trait value = arg._unpack();
        List<Expr> finElements = new ArrayList<>();
        finElements.add(TraitFw.trait.asVal().toExpr(toExpr));
        List<Expr> elements = new ArrayList<>();
        for (Val val : value.fields) {
            elements.add(val.toExpr(toExpr));
        }
        finElements.add(ExprList.of(BracketsTypes.square, elements));
        return ExprFw.wrap(ExprList.of(BracketsTypes.round, finElements));
    }

    private static final class Trait {
        private final Val[] fields;
        private final Val constraint;

        private Trait(Val[] fields) {
            this.fields = fields;
            this.constraint = toConstraint(this);
        }

        private static Val toConstraint(Trait trait) {
            Vit a = Vit.val(BoolFw._true);
            for (Val field : trait.fields) {
                Val key = DeclarationFw.getKey(field);
                Val constraint = DeclarationFw.getConstraint(field);
                Vit fieldChecker = Vit.call(constraint, symbol("check")).call(Vit.call(Vit.var, key));
                a = a.call(symbol("and")).call(fieldChecker);
            }
            Vit b = Vit.val(BoolFw._true);
            return ConstraintFw.constraintBuilder
                    .call(VitFw.wrap(Vit.call(EqFw.eq, a).call(b)));
        }

        public int indexOf(Val key) {
            for (int i = 0; i < fields.length; i++) {
                Val field = fields[i];
                if (DeclarationFw.getKey(field).equals(key))
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
