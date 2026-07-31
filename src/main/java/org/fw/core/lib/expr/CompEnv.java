package org.fw.core.lib.expr;

import org.fw.core.base.*;
import org.fw.core.base.context.Context;
import org.fw.core.lib.ChainLinkFw;
import org.fw.core.adapter.AbstractValAdapted;
import org.fw.core.ast.Expr;
import org.fw.core.lib.VitFw;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;

import static org.fw.core.FW.symbol;

public final class CompEnv extends AbstractValAdapted {

    public static final Type compEnv = ChainLinkFw.chainLinkType.asVal()
            .call(symbol("constructor"), Context.outOf)
            .call(Unspecified.isNot, Context.outOf)
            .asType();

    private CompEnv(Val val) {
        super(val);
    }

    public static CompEnv of(Val val) {
        return new CompEnv(val);
    }

    public Val compileV(Expr expr, Context context) {
        return compileV(ExprFw.wrap(expr), context);
    }

    public Vit compile(Expr expr, Context context) throws VitCompilationException {
        return compile(ExprFw.wrap(expr), context);
    }

    public Val compileV(Val expr, Context context) {
        return asVal().call(syntaxResolve(expr._unpack(), this), context);
    }

    public Vit compile(Val expr, Context context) throws VitCompilationException {
        Val v = asVal().call(syntaxResolve(expr._unpack(), this), context);
        return VitFw.unwrap(v);
    }

    public static Val syntaxResolve(Expr expr, CompEnv env) {
        return Val.of(SyntaxResolveFw.syntaxResolve, new SyntaxResolveFw.SyntaxResolve(expr, env));
    }

    public static Val compEnv(Val resolver, Val parentCEnv, Context context) {
        return ChainLinkFw.chain(compEnv, resolver, parentCEnv, context);
    }

    public static Val compEnv(Context context, Val... resolvers) {
        return ChainLinkFw.chain(compEnv, context, resolvers);
    }
}
