package org.fw.core.lib.expr;

import org.fw.core.FW;
import org.fw.core.util.FwUtils;
import org.fw.core.adapter.AbstractValAdapted;
import org.fw.core.ast.Expr;
import org.fw.core.base.Call;
import org.fw.core.base.Context;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.VitFw;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;

import static org.fw.core.FW.symbol;

public final class CompEnv extends AbstractValAdapted {
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
        Val v = asVal().call(syntaxResolve(expr._unpack(), this), context);
        return v;
    }

    public Vit compile(Val expr, Context context) throws VitCompilationException {
        Val v = asVal().call(syntaxResolve(expr._unpack(), this), context);
        return VitFw.unwrap(v);
    }

    public static Val syntaxResolve(Expr expr, CompEnv env) {
        return Val.of(SyntaxResolveFw.syntaxResolve, new SyntaxResolveFw.SyntaxResolve(expr, env));
    }

    public static final Type compEnv = FW.telephonist("CompEnv", (arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, CompEnv.compEnv, context)) {
            SyntaxResolveFw.CompEnvRecord instance = Call.getVal(arg, context)._unpack();
            Val cArg = Call.getArg(arg, context);

            if (cArg.type().equals(SyntaxResolveFw.syntaxResolve)) {
                Val ret = instance.resolver().call(cArg, context);
                if (ret == Val.unspecified)
                    return instance.parentCEnv().call(cArg, context);
                return ret;
            }
        } else if (arg.equals(symbol("builder"))) {
            return FW.telephonist("CompEnv.builder", (resolver, context1) -> {
                return FW.telephonist(() -> "(call CompEnv.builder " + resolver.toExpr(context1) + ")", (parentCEnv, c) -> {
                    return Val.of(CompEnv.compEnv, new SyntaxResolveFw.CompEnvRecord(resolver, parentCEnv));
                });
            });
        }

        return Val.unspecified;
    }).asType();

    public static Val compEnv(Val resolver, Val parentCEnv, Context context) {
        return compEnv.asVal().call(symbol("builder"), context).call(resolver, context).call(parentCEnv, context);
    }

    public static Val compEnv(Context context, Val... resolvers) {
        Val actual = resolvers[0];
        for (int i = 1; i < resolvers.length; i++) {
            actual = compEnv(actual, resolvers[i], context);
        }
        return actual;
    }

}
