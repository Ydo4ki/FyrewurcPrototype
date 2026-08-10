package org.fw.core.lib;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.base.context.RtEnv;
import org.fw.core.lib.expr.ExprFw;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.lib.constraint.ConstraintFw;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.ExprCallOpFw;
import org.fw.core.vit.Vit;

import java.math.BigInteger;
import java.util.Objects;

import static org.fw.core.FW.symbol;

public final class DeclarationFw {

    // I hope it will be possible to make it a struct later
    public static final Type declaration = FW.telephonist("Declaration", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, DeclarationFw.declaration)) {
            Val instance = Call.getVal(arg);
            arg = Call.getArg(arg);

            Declaration decl = instance._unpack();
            if (arg.equals(symbol("key"))) {
                return decl.key();
            } else if (arg.equals(symbol("constraint"))) {
                return decl.constraint();
            }
        } else if (arg.equals(symbol("builder"))) {
            return FW.telephonist("Declaration.builder", (key) -> {
                return FW.telephonist(() -> "(call Declaration.builder " + key + ")", (constraint) -> {
                    if (!ConstraintFw.isConstraint(constraint)) return null;
                    return Val.of(DeclarationFw.declaration, new Declaration(key, constraint));
                });
            });
        }
        return null;
    }).asType();
    public static final Val declarationToExpr = FW.telephonist((arg) -> {
        Type type = arg.type();
        if (type.equals(declaration)) {
            Expr expr = toExpr(arg, RtEnv.unspecified); // todo
            return ExprFw.wrap(expr);
        }
        return null;
    });

    public static Val getKey(Val declaration) {
        return declaration.call(symbol("key"));
    }

    public static Val getConstraint(Val declaration) {
        return declaration.call(symbol("constraint"));
    }

    public static Val declaration(Val key, Val constraint) {
        if (!ConstraintFw.isConstraint(constraint))
            throw new IllegalArgumentException();
        return Val.of(declaration, new Declaration(key, constraint));
    }

    public static Expr toExpr(Val arg, RtEnv rtEnv) {
        return arg._unpack(DeclarationFw.Declaration.class).toExpr(rtEnv);
    }

    private static final class Declaration {
        private final Val key;
        private final Val constraint;

        private Declaration(Val key, Val constraint) {
            this.key = key;
            this.constraint = constraint;
        }

        public Expr toExpr(RtEnv rtEnv) {
            return ExprList.of(BracketsTypes.round, Symbol.of("Declaration"), key.toExpr(rtEnv), constraint.toExpr(rtEnv));
        }

        public Val key() {
            return key;
        }

        public Val constraint() {
            return constraint;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            Declaration that = (Declaration) obj;
            return Objects.equals(this.key, that.key) &&
                    Objects.equals(this.constraint, that.constraint);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, constraint);
        }

        @Override
        public String toString() {
            return "Declaration[" +
                    "key=" + key + ", " +
                    "constraint=" + constraint + ']';
        }
    }
}
