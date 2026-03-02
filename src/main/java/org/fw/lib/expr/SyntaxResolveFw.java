package org.fw.lib.expr;

import org.fw.FW;
import org.fw.FwUtils;
import org.fw.ast.BracketsTypes;
import org.fw.ast.Expr;
import org.fw.ast.ExprList;
import org.fw.ast.Symbol;
import org.fw.base.Call;
import org.fw.base.Context;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.lib.BoxFw;
import org.fw.lib.DIntFw;
import org.fw.lib.VitFw;
import org.fw.vit.Vit;

import java.math.BigInteger;

import static org.fw.FW.symbol;

public final class SyntaxResolveFw {
    public static final Type syntaxResolve = FW.telephonist("SyntaxResolve", (arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, SyntaxResolveFw.syntaxResolve, context)) {
            Val instance = Call.getVal(arg, context);
            Val cArg = Call.getArg(arg, context);

            SyntaxResolve sr = instance._unpack();
            if (cArg.equals(symbol("expr"))) {
                return ExprFw.wrap(sr.expr());
            } else if (cArg.equals(symbol("comp-env"))) {
                return sr.env().asVal();
            }
        } else if (arg.equals(symbol("builder"))) {
            return FW.telephonist("SyntaxResolve.builder", (expr, context1) -> {
                if (!expr.type().equals(ExprFw.symbol) && !expr.type().equals(ExprFw.exprList)) {
                    return Val.unspecified;
                }
                return FW.telephonist(() -> "(SyntaxResolve.builder " + expr.toExpr(context1) + ")", (callerEnv, context2) -> {
                    return Val.of(SyntaxResolveFw.syntaxResolve, new SyntaxResolve(expr._unpack(), CompEnv.of(callerEnv)));
                });
            });
        } else if (arg.type().equals(ExprFw.toExpr)) {
            Val instance = BoxFw.unbox(arg);
            if (!instance.type().equals(SyntaxResolveFw.syntaxResolve))
                return Val.unspecified;

            return ExprFw.wrap(instance._unpack(SyntaxResolve.class).toExpr(context));
        } else if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"), context);
            Val cEnv = arg.call(symbol("comp-env"), context);
            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 2) return Val.unspecified;

            Val retVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(0), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(retVit.type()))
                return retVit; // compile error idk

            Val retVit2 = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(retVit2.type()))
                return retVit2; // compile error idk

            Vit vit = Vit.val(SyntaxResolveFw.syntaxResolve.asVal()).call(symbol("builder"))
                    .call(VitFw.unwrap(retVit))
                    .call(VitFw.unwrap(retVit2));

            return VitFw.wrap(vit);
        }
        return Val.unspecified;
    }).asType();

    record SyntaxResolve(Expr expr, CompEnv env) {
        public Expr toExpr(Context context) {
            return ExprList.of(BracketsTypes.round,
                    SyntaxResolveFw.syntaxResolve.asVal().toExpr(context),
                    ExprList.of(BracketsTypes.round, ExprFw.expr.toExpr(context), expr),
                    env.asVal().toExpr(context)
            );
        }
    }

    record CompEnvRecord(Val resolver, Val parentCEnv) {
        public Expr toExpr(Context context) {
            return ExprList.of(BracketsTypes.round, Symbol.of("comp-env"), resolver.toExpr(context), parentCEnv.toExpr(context));
        }
    }
}
