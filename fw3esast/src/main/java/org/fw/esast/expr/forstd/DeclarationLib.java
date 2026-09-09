package org.fw.esast.expr.forstd;

import com.ydo4ki.esast.BracketsTypes;
import com.ydo4ki.esast.Expr;
import com.ydo4ki.esast.ExprList;
import com.ydo4ki.esast.Symbol;
import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import org.fw.base.SymbolFw;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.core.FW;
import org.fw.core.state.operation.Operation;
import org.fw.core.vit.Vit;
import org.fw.esast.expr.*;
import org.fw.std.DeclarationFw;
import org.fw.std.DeclaredFw;
import org.fw.std.ModuleFw;
import org.fw.std.VitFw;
import org.fw.std.dvec.DVecFw;

import static org.fw.core.FW.symbol;

public final class DeclarationLib {
    public static final CompEnv directivesCenv = CompEnv.of(FW.lambda_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.toExprResolve)) {
            CompEnv compEnv = CompEnv.of(arg.get("chain"));
            arg = (Val) arg.get("passing");

            Type type = arg.getType();
            if (type.equals(DeclarationFw.declaration)) {
                Val key = (Val) arg.get("key");
                return ExprFw.wrap(toExpr(arg, compEnv));
            }
            return null;
        } else if (arg.getType().equals(SyntaxResolveFw.toFnResolve)) {
            Val val = (Val) arg.get("passing");
            Val compEnv = (Val) arg.get("chain");
            if (val == DeclarationFw.declaration.asVal()) {
                return FW.lambda_native(c -> {
                    if (c.getType() != DVecFw.dVec)
                        return null;
                    Val[] args = c._UNPACK_();
                    if (args.length > 2)
                        return null;
                    Val val1 = DeclarationFw.declaration.asVal();
                    Val b = (Val) val1.get("builder");
                    for (Val arg1 : args) {
                        b = (Val) b.call(arg1);
                    }
                    return Operation.pure(b).asVal();
                });
            }
        } else if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = (Val) arg.call(FW.symbol("expr"));
            Val compEnv = (Val) arg.call(FW.symbol("comp-env"));
            Expr expr = (Expr) ExprFw.unwrap(exprVal);
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case "=": {
                        if (isize != 3)
                            return VitErrorFw.rrror(f, "3 elements expected");

                        Val name = (Val) exprVal.call(DIntFw.dint(1));
                        if (!name.getType().equals(SymbolFw.symbol))
                            return VitErrorFw.rrror(ExprFw.unwrap(name), "Symbol expected"); // symbol expected

                        Val val = ((Val) exprVal.call(DIntFw.dint(2)));
                        Val value = (Val) compEnv.call(CompEnv.syntaxResolve((Expr) ExprFw.unwrap(val), CompEnv.of(compEnv)));
                        if (!VitFw.isVit(value.getType()))
                            return value; // error idk

                        return VitFw.wrap(Vit.val(DeclarationFw.declaration.asVal()).call(symbol("builder")).call(name).call((Vit) value._UNPACK_()));
                    }
                }
            }
        }
        return null;
    }));
    public static final Lib lib = Lib.of(
            ModuleFw.module(
                    DeclaredFw.declared(symbol("Declaration"), DeclarationFw.declaration.asVal())
            ),
            directivesCenv.asValue()
    );

    public static Expr toExpr(Val arg, CompEnv toExpr) {
        DeclarationFw.Declaration self = ((DeclarationFw.Declaration) arg._UNPACK_());
        if (self.key().getType() == SymbolFw.symbol)
            return ExprList.of(BracketsTypes.round, Symbol.of("="), ExprFw.unwrap(self.key()), toExpr.toExpr(self.constraint()));
        return ExprList.of(BracketsTypes.round, Symbol.of("Declaration"), toExpr.toExpr(self.key()), toExpr.toExpr(self.constraint()));
    }
}
