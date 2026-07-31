package org.fw.core.lib;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.base.context.Context;
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
    @Deprecated
    public static final Val field = FW.telephonist("=", (arg, context) -> {
        if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"), context);
            Val cEnv = arg.call(symbol("comp-env"), context);

            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 2)
                return null;

            Val name = arg.call(DIntFw.dint(0), context);
            if (!name.type().equals(SymbolFw.symbol))
                return null; // symbol expected

            Val value = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(value.type())) return value; // error idk

            return VitFw.wrap(Vit.val(DeclarationFw.declaration.asVal()).call(symbol("builder")).call(name)
                    .call(Vit.call(ConstraintFw.to_constraint, VitFw.unwrap0(value))));
        }
        return null;
    });

    // I hope it will be possible to make it a struct later
    public static final Type declaration = FW.telephonist("Declaration", (arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, DeclarationFw.declaration, context)) {
            Val instance = Call.getVal(arg, context);
            arg = Call.getArg(arg, context);

            Declaration decl = instance._unpack();
            if (arg.equals(symbol("key"))) {
                return decl.key();
            } else if (arg.equals(symbol("constraint"))) {
                return decl.constraint();
            }
        } else if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"), context);
            Val cEnv = arg.call(symbol("comp-env"), context);

            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 2)
                return null;

            Val name = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(0), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(name.type()))
                return name; // error idk

            Val value = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(value.type()))
                return value; // error idk

            return VitFw.wrap(Vit.val(DeclarationFw.declaration.asVal()).call(symbol("builder")).call(VitFw.unwrap0(name)).call(VitFw.unwrap0(value)));
        } else if (arg.equals(symbol("builder"))) {
            return FW.telephonist("Declaration.builder", (key, context1) -> {
                return FW.telephonist(() -> "(call Declaration.builder " + key + ")", (constraint, context2) -> {
                    if (!ConstraintFw.isConstraint(constraint)) return null;
                    return Val.of(DeclarationFw.declaration, new Declaration(key, constraint));
                });
            });
        }
        return null;
    }).asType();
    public static final Val declarationToExpr = FW.telephonist((arg, context) -> {
        Type type = arg.type();
        if (type.equals(declaration)) {
            Expr expr = toExpr(arg, context);
            return ExprFw.wrap(expr);
        }
        return null;
    });

    public static Val getKey(Val declaration, Context context) {
        return declaration.call(symbol("key"), context);
    }

    public static Val getConstraint(Val declaration, Context context) {
        return declaration.call(symbol("constraint"), context);
    }

    public static Val declaration(Val key, Val constraint) {
        if (!ConstraintFw.isConstraint(constraint))
            throw new IllegalArgumentException();
        return Val.of(declaration, new Declaration(key, constraint));
    }

    public static Expr toExpr(Val arg, Context context) {
        return arg._unpack(DeclarationFw.Declaration.class).toExpr(context);
    }

    private static final class Declaration {
        private final Val key;
        private final Val constraint;

        private Declaration(Val key, Val constraint) {
            this.key = key;
            this.constraint = constraint;
        }

        public Expr toExpr(Context context) {
            return ExprList.of(BracketsTypes.round, Symbol.of("Declaration"), key.toExpr(context), constraint.toExpr(context));
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
