package org.fw.lib;

import org.fw.FW;
import org.fw.FwUtils;
import org.fw.annotation.Insightful;
import org.fw.ast.BracketsTypes;
import org.fw.ast.Expr;
import org.fw.ast.ExprList;
import org.fw.ast.Symbol;
import org.fw.base.Call;
import org.fw.base.Context;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.lib.constraint.ConstraintFw;
import org.fw.lib.expr.CompEnv;
import org.fw.lib.expr.ExprCallOpFw;
import org.fw.lib.expr.ExprFw;
import org.fw.vit.Vit;

import java.math.BigInteger;

import static org.fw.FW.symbol;

public final class DeclarationFw {
    @Insightful
    public static final Val field = FW.telephonist("=", (arg, context) -> {
        if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"), context);
            Val cEnv = arg.call(symbol("comp-env"), context);

            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 2)
                return Val.unspecified;

            Val name = arg.call(DIntFw.dint(0), context);
            if (!name.type().equals(ExprFw.symbol))
                return Val.unspecified; // symbol expected

            Val value = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(value.type())) return value; // error idk

            return VitFw.wrap(Vit.val(DeclarationFw.declaration.asVal()).call(symbol("builder")).call(name)
                    .call(Vit.call(ConstraintFw.to_constraint, VitFw.unwrap(value))));
        }
        return Val.unspecified;
    });

    // I hope it will be possible to make it a struct later
    @Insightful
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
                return Val.unspecified;

            Val name = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(0), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(name.type()))
                return name; // error idk

            Val value = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(value.type())) return value; // error idk

            return VitFw.wrap(Vit.val(DeclarationFw.declaration.asVal()).call(symbol("builder")).call(VitFw.unwrap(name)).call(VitFw.unwrap(value)));
        } else if (arg.equals(symbol("builder"))) {
            return FW.telephonist("Declaration.builder", (key, context1) -> {
                return FW.telephonist(() -> "(call Declaration.builder " + key + ")", (constraint, context2) -> {
                    if (!ConstraintFw.isConstraint(constraint)) return Val.unspecified;
                    return Val.of(DeclarationFw.declaration, new Declaration(key, constraint));
                });
            });
        }
        return Val.unspecified;
    }).asType();

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

    private record Declaration(Val key, Val constraint) {
        public Expr toExpr(Context context) {
            return ExprList.of(BracketsTypes.round, Symbol.of("Declaration"), key.toExpr(context), constraint.toExpr(context));
        }
    }
}
