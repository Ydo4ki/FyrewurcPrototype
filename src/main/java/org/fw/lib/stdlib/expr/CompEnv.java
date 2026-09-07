package org.fw.lib.stdlib.expr;

import org.fw.core.abstrait.Value;
import org.fw.core.base.*;
import org.fw.core.commons.AbstractValueAdapter;
import org.fw.lib.stdlib.ChainLinkFw;
import org.fw.core.ast.Expr;
import org.fw.lib.stdlib.ChainResolveFw;
import org.fw.lib.stdlib.VitFw;
import org.fw.lib.stdlib.ConstraintFw;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;

import static org.fw.core.FW.symbol;

public final class CompEnv extends AbstractValueAdapter {

    public static final Type compEnv = ChainLinkFw.chainLinkType.asVal()
            .call(symbol("construct"))
            .call(ConstraintFw.isSpecified)
            .asType();

    private CompEnv(Value val) {
        super(val);
    }

    public static CompEnv of(Value val) {
        return new CompEnv(val);
    }

    public Value compileV(Expr expr) {
        return compileV(ExprFw.wrap(expr));
    }

    public Vit compile(Expr expr) throws VitCompilationException {
        return compile(ExprFw.wrap(expr));
    }

    public Value compileV(Val expr) {
        return asValue().call(syntaxResolve(expr._UNPACK_(Expr.class), this));
    }

    public Vit compile(Val expr) throws VitCompilationException {
        Value v = asValue().call(syntaxResolve(expr._UNPACK_(Expr.class), this));
        return VitFw.unwrap((Val) v, expr._UNPACK_(Expr.class));
    }

    public static Val syntaxResolve(Expr expr, CompEnv env) {
        return Val._NEW_INSTANCE_(SyntaxResolveFw.syntaxResolve, new ChainResolveFw.ChainResolve(ExprFw.wrap(expr), (Val) env.asValue()));
    }

    public Expr toExpr(Value val) {
        Value v = asValue().call(toExprResolve((Val) val, this));
        return ExprFw.unwrap((Val) v);
    }

    public static Val toExprResolve(Val val, CompEnv env) {
        return Val._NEW_INSTANCE_(SyntaxResolveFw.toExprResolve, new ChainResolveFw.ChainResolve(val, (Val) env.asValue()));
    }

    public static Vit toExprResolve(Vit val, CompEnv env) {
        return Vit.val(SyntaxResolveFw.toExprResolve.asVal()).call(symbol("builder")).call(val).call(env.asValue());
    }

    public static Vit toFnResolve(Vit val, CompEnv env) {
        return Vit.val(SyntaxResolveFw.toFnResolve.asVal()).call(symbol("builder")).call(val).call(env.asValue());
    }

    public static Value compEnv(Value parentCEnv, Value resolver) {
        return ChainLinkFw.chain(compEnv, parentCEnv, resolver);
    }

    public static Value compEnv(Value... resolvers) {
        return ChainLinkFw.chain(compEnv, resolvers);
    }
}
