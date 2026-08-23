package org.fw.lib.elib;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.lib.elib.expr.ExprFw;
import org.fw.lib.elib.expr.ToExprFn;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.lib.elib.constraint.ConstraintFw;

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
        if (arg.type() != ToExprFn.toExprResolve)
            return null;
        Val toExpr = arg.call(symbol("chain"));
        arg = arg.call(symbol("passing"));

        Type type = arg.type();
        if (type.equals(declaration)) {
            Expr expr = toExpr(arg, toExpr); // todo
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

    public static Expr toExpr(Val arg, Val toExpr) {
        return arg._unpack(DeclarationFw.Declaration.class).toExpr(toExpr);
    }

    private static final class Declaration {
        private final Val key;
        private final Val constraint;

        private Declaration(Val key, Val constraint) {
            this.key = key;
            this.constraint = constraint;
        }

        public Expr toExpr(Val toExpr) {
            return ExprList.of(BracketsTypes.round, Symbol.of("Declaration"), key.toExpr(toExpr), constraint.toExpr(toExpr));
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
