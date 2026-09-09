package org.fw.esast.expr;

import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.core.abstrait.Value;
import org.fw.core.commons.AbstractValueAdapter;
import org.fw.esast.expr.forstd.VitLib;
import org.fw.std.ChainLinkFw;
import com.ydo4ki.esast.Expr;
import org.fw.std.ChainResolveFw;
import com.ydo4ki.fw.internal.lib.ConstraintFw;
import org.fw.core.vit.Vit;
import org.fw.esast.ExprVitCompilationException;

import static org.fw.core.FW.symbol;

public final class CompEnv extends AbstractValueAdapter {

    public static final Type compEnv;

    static {
        Val val = ChainLinkFw.chainLinkType.asVal();
        Val val1 = ((Val) val.call(symbol("construct")));
        compEnv = ((Val) (Val) val1.call(ConstraintFw.isSpecified))
                .asType();
    }

    private CompEnv(Value val) {
        super(val);
    }

    public static CompEnv of(Value val) {
        return new CompEnv(val);
    }

    public Value compileV(Expr expr) {
        return compileV(ExprFw.wrap(expr));
    }

    public Vit compile(Expr expr) throws ExprVitCompilationException {
        return compile(ExprFw.wrap(expr));
    }

    public Value compileV(Val expr) {
        return asValue().call(syntaxResolve((Expr) ExprFw.unwrap(expr), this));
    }

    public Vit compile(Val expr) throws ExprVitCompilationException {
        Value v = asValue().call(syntaxResolve((Expr) ExprFw.unwrap(expr), this));
        return VitLib.unwrap((Val) v, (Expr) ExprFw.unwrap(expr));
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
