package org.fw.core.lib.expr;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.base.context.Context;
import org.fw.core.base.context.RtEnv;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.lib.DIntFw;
import org.fw.core.lib.VitFw;
import org.fw.core.vit.Vit;

import java.math.BigInteger;

import static org.fw.core.FW.symbol;

// todo: replace with more general ChainResolve
public final class SyntaxResolveFw {
    public static final Type syntaxResolve = FW.telephonist("SyntaxResolve", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, SyntaxResolveFw.syntaxResolve)) {
            Val instance = Call.getVal(arg);
            Val cArg = Call.getArg(arg);

            SyntaxResolve sr = instance._unpack();
            if (cArg.equals(symbol("expr"))) {
                return ExprFw.wrap(sr.expr());
            } else if (cArg.equals(symbol("comp-env"))) {
                return sr.env().asVal();
            }
        } else if (arg.equals(symbol("builder"))) {
            return FW.telephonist("SyntaxResolve.builder", (expr) -> {
                if (!expr.type().equals(SymbolFw.symbol) && !expr.type().equals(ExprFw.exprList)) {
                    return null;
                }
                return FW.telephonist((callerEnv) -> {
                    return Val.of(SyntaxResolveFw.syntaxResolve, new SyntaxResolve(expr._unpack(), CompEnv.of(callerEnv)));
                });
            });
        } else if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"));
            Val cEnv = arg.call(symbol("comp-env"));
            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 2) return null;

            Val retVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(0))._unpack(), CompEnv.of(cEnv)));
            if (!VitFw.isVit(retVit.type()))
                return retVit; // compile error idk

            Val retVit2 = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(1))._unpack(), CompEnv.of(cEnv)));
            if (!VitFw.isVit(retVit2.type()))
                return retVit2; // compile error idk

            Vit vit = Vit.val(SyntaxResolveFw.syntaxResolve.asVal()).call(symbol("builder"))
                    .call(VitFw.unwrap(retVit))
                    .call(VitFw.unwrap(retVit2));

            return VitFw.wrap(vit);
        }
        return null;
    }).asType();
    public static final Val syntaxResolveToExpr = FW.telephonist((arg) -> {
        Type type = arg.type();
        if (type.equals(syntaxResolve)) {
            return ExprFw.wrap(arg._unpack(SyntaxResolve.class).toExpr(RtEnv.unspecified));
        }
        return null;
    });

    static final class SyntaxResolve {
        private final Expr expr;
        private final CompEnv env;

        SyntaxResolve(Expr expr, CompEnv env) {
            this.expr = expr;
            this.env = env;
        }

        public Expr expr() {
            return expr;
        }

        public CompEnv env() {
            return env;
        }

        public Expr toExpr(RtEnv rtEnv) {
            return ExprList.of(BracketsTypes.round,
                    SyntaxResolveFw.syntaxResolve.asVal().toExpr(rtEnv),
                    ExprList.of(BracketsTypes.round, ExprFw.expr.toExpr(rtEnv), expr),
                    env.asVal().toExpr(rtEnv)
            );
        }
    }

}
