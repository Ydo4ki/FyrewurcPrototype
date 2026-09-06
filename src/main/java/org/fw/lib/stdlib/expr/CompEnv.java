package org.fw.lib.stdlib.expr;

import org.fw.core.abstrait.Value;
import org.fw.core.base.*;
import org.fw.lib.stdlib.ChainLinkFw;
import org.fw.core.commons.AbstractValAdapter;
import org.fw.core.ast.Expr;
import org.fw.lib.stdlib.ChainResolveFw;
import org.fw.lib.stdlib.VitFw;
import org.fw.lib.stdlib.ConstraintFw;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;

import static org.fw.core.FW.symbol;

public final class CompEnv extends AbstractValAdapter {

    public static final Type compEnv = ChainLinkFw.chainLinkType.asVal()
            .call(symbol("construct"))
            .call(ConstraintFw.isSpecified)
            .asType();

    private CompEnv(Val val) {
        super(val);
    }

    public static CompEnv of(Value val) {
        return new CompEnv((Val)val); // todo
    }

    public Val compileV(Expr expr) {
        return compileV(ExprFw.wrap(expr));
    }

    public Vit compile(Expr expr) throws VitCompilationException {
        return compile(ExprFw.wrap(expr));
    }

    public Val compileV(Val expr) {
        return asVal().call(syntaxResolve(expr._UNPACK(Expr.class), this));
    }

    public Vit compile(Val expr) throws VitCompilationException {
        Val v = asVal().call(syntaxResolve(expr._UNPACK(Expr.class), this));
        return VitFw.unwrap(v, expr._UNPACK(Expr.class));
    }

    public static Val syntaxResolve(Expr expr, CompEnv env) {
        return Val.of(SyntaxResolveFw.syntaxResolve, new ChainResolveFw.ChainResolve(ExprFw.wrap(expr), env.asVal()));
    }

    public Expr toExpr(Value val) {
        Val v = asVal().call(toExprResolve((Val) val, this));
        return ExprFw.unwrap(v);
    }

    public static Val toExprResolve(Val val, CompEnv env) {
        return Val.of(SyntaxResolveFw.toExprResolve, new ChainResolveFw.ChainResolve(val, env.asVal()));
    }

    public static Vit toExprResolve(Vit val, CompEnv env) {
        return Vit.val(SyntaxResolveFw.toExprResolve.asVal()).call(symbol("builder")).call(val).call(env.asVal());
    }

    public static Vit toFnResolve(Vit val, CompEnv env) {
        return Vit.val(SyntaxResolveFw.toFnResolve.asVal()).call(symbol("builder")).call(val).call(env.asVal());
    }

    public static Value compEnv(Value parentCEnv, Value resolver) {
        return ChainLinkFw.chain(compEnv, parentCEnv, resolver);
    }

    public static Value compEnv(Value... resolvers) {
        return ChainLinkFw.chain(compEnv, resolvers);
    }
}
