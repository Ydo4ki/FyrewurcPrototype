package org.fw.core.lib.expr;

import org.fw.core.base.*;
import org.fw.core.lib.ChainLinkFw;
import org.fw.core.commons.AbstractValAdapted;
import org.fw.core.ast.Expr;
import org.fw.core.lib.ChainResolveFw;
import org.fw.core.lib.VitFw;
import org.fw.core.lib.constraint.ConstraintFw;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;

import static org.fw.core.FW.symbol;

public final class CompEnv extends AbstractValAdapted {

    public static final Type compEnv = ChainLinkFw.chainLinkType.asVal()
            .call(symbol("constructor"))
            .call(ConstraintFw.isSpecified)
            .asType();

    private CompEnv(Val val) {
        super(val);
    }

    public static CompEnv of(Val val) {
        return new CompEnv(val);
    }

    public Val compileV(Expr expr) {
        return compileV(ExprFw.wrap(expr));
    }

    public Vit compile(Expr expr) throws VitCompilationException {
        return compile(ExprFw.wrap(expr));
    }

    public Val compileV(Val expr) {
        return asVal().call(syntaxResolve(expr._unpack(), this));
    }

    public Vit compile(Val expr) throws VitCompilationException {
        Val v = asVal().call(syntaxResolve(expr._unpack(), this));
        return VitFw.unwrap(v);
    }

    public static Val syntaxResolve(Expr expr, CompEnv env) {
        return Val.of(SyntaxResolveFw.syntaxResolve, new ChainResolveFw.ChainResolve(ExprFw.wrap(expr), env.asVal()));
    }

    public static Val compEnv(Val resolver, Val parentCEnv) {
        return ChainLinkFw.chain(compEnv, resolver, parentCEnv);
    }

    public static Val compEnv(Val... resolvers) {
        return ChainLinkFw.chain(compEnv, resolvers);
    }
}
