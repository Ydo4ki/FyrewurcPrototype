package org.fw.esast.expr.forstd;

import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import org.fw.core.vit.*;
import org.fw.esast.ExprVitCompilationException;
import com.ydo4ki.esast.BracketsTypes;
import com.ydo4ki.esast.ExprList;
import com.ydo4ki.esast.Symbol;
import com.ydo4ki.esast.Expr;
import org.fw.esast.expr.*;
import org.fw.esast.util.FwUtils3;
import org.fw.std.DeclaredFw;
import org.fw.std.ModuleFw;
import org.fw.std.VitFw;

import java.util.ArrayList;
import java.util.List;

import static org.fw.core.FW.symbol;

public final class VitLib {
    public static final CompEnv vit2exprCenv = CompEnv.of(FW.lambda_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.toExprResolve)) {
            CompEnv compEnv = CompEnv.of((Val) arg.get("chain"));
            arg = (Val) arg.get("passing");

            Type type = arg.getType();
            if (type.equals(VitFw.vitVal)) {
                VitVal vitVal = arg._UNPACK_();
                Value value = type.asVal();
                return ExprFw.wrap(ExprList.of(BracketsTypes.round, compEnv.toExpr(value), compEnv.toExpr(vitVal.val())));
            } else if (type.equals(VitFw.vitVar)) {
                Value value = type.asVal();
                return ExprFw.wrap(ExprList.of(BracketsTypes.round, compEnv.toExpr(value)));
            } else if (type.equals(VitFw.vitCall)) {
                VitCall vitVal = arg._UNPACK_();
                List<Expr> elements = new ArrayList<>();
                Value value = type.asVal();
                elements.add(compEnv.toExpr(value));
                elements.addAll(FwUtils3.exprs(vitVal, compEnv));

                return ExprFw.wrap(ExprList.of(BracketsTypes.round, elements));
            } else if (type.equals(VitFw.vitInvoke)) {
                VitInvoke vitInvoke = arg._UNPACK_();
                Value value = VitFw.wrap(vitInvoke.operation());
                Value value1 = type.asVal();
                return ExprFw.wrap(ExprList.of(BracketsTypes.round, compEnv.toExpr(value1), compEnv.toExpr(value)));
            }
            return null;
        }
        return null;
    }));
    public static final CompEnv directivesCenv = CompEnv.of(FW.lambda_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = (Val) arg.call((Value) FW.symbol("expr"));
            Val compEnv = (Val) arg.call((Value) FW.symbol("comp-env"));
            Expr expr = (Expr) ExprFw.unwrap(exprVal);
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case "var": {
                        if (isize != 1) {
                            return null;
                        }
                        return VitFw.wrap(Vit.var);
                    }
                    case "call": {
                        if (isize == 1) {
                            return null;
                        }
                        Val val1 = ((Val) exprVal.call((Value) DIntFw.dint(1)));
                        Expr eee = (Expr) ExprFw.unwrap(val1);
                        Val retVit = (Val) compEnv.call((Value) CompEnv.syntaxResolve(eee, CompEnv.of(compEnv)));
                        if (!VitFw.isVit(retVit.getType()))
                            return retVit; // compile error idk

                        for (int i = 1; i < (isize - 1); i++) {
                            Val val = ((Val) exprVal.call((Value) DIntFw.dint(i + 1)));
                            Expr eeeN = (Expr) ExprFw.unwrap(val);
                            Val argNVit = (Val) compEnv.call((Value) CompEnv.syntaxResolve(eeeN, CompEnv.of(compEnv)));
                            if (!VitFw.isVit(argNVit.getType()))
                                return argNVit; // compile error idk

                            try {
                                retVit = VitFw.wrap(unwrap(retVit, eee).call(unwrap(argNVit, eeeN)));
                            } catch (ExprVitCompilationException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        return retVit;
                    }
                    case "invoke!": {
                        if (isize != 2) {
                            return null;
                        }

                        Val val = ((Val) exprVal.call((Value) DIntFw.dint(1)));
                        Val retVit = (Val) compEnv.call((Value) CompEnv.syntaxResolve((Expr) ExprFw.unwrap(val), CompEnv.of(compEnv)));
                        if (!VitFw.isVit(retVit.getType()))
                            return retVit; // compile error idk

                        Vit vit = VitUtils.simplify(retVit._UNPACK_());

                        return VitFw.wrap(Vit.invoke(vit));
                    }
                    case "compile-vit": {
                        if (isize != 2)
                            return null;

                        Val val = ((Val) exprVal.call((Value) DIntFw.dint(1)));
                        return VitFw.wrap(VitUtils.simplify(Vit.val(
                                (Val) compEnv.call((Value) CompEnv.syntaxResolve((Expr) ExprFw.unwrap(val), CompEnv.of(compEnv)))
                        )));
                    }
                    case "compile-vit-fast": {
                        if (isize != 2)
                            return null;

                        Val val = ((Val) exprVal.call((Value) DIntFw.dint(1)));
                        return VitFw.wrap(Vit.val(
                                (Val) compEnv.call((Value) CompEnv.syntaxResolve((Expr) ExprFw.unwrap(val), CompEnv.of(compEnv)))
                        ));
                    }
                }
            }
        }
        return null;
    }));
    public static final Lib lib = Lib.of(
            ModuleFw.module(
                    DeclaredFw.declared(symbol("VitVal"), VitFw.vitVal.asVal()),
                    DeclaredFw.declared(symbol("VitVar"), VitFw.vitVar.asVal()),
                    DeclaredFw.declared(symbol("VitCall"), VitFw.vitCall.asVal()),
                    DeclaredFw.declared(symbol("VitInvoke"), VitFw.vitInvoke.asVal()),
                    DeclaredFw.declared(symbol("eval-vit"), VitFw.evalVit)
            ),
            CompEnv.compEnv(
                    vit2exprCenv.asValue(),
                    directivesCenv.asValue()
            )
    );

    public static Vit unwrap(Val vit, Expr expr) throws ExprVitCompilationException {
        if (
                vit.getType().equals(VitFw.vitVal)
                        || vit.getType().equals(VitFw.vitVar)
                        || vit.getType().equals(VitFw.vitCall)
                        || vit.getType().equals(VitFw.vitInvoke)
        ) {
            return vit._UNPACK_();
        }
        if (vit.getType().equals(VitErrorFw.vitError))
            //noinspection DataFlowIssue
            throw new ExprVitCompilationException(ExprFw.unwrap((Val) vit.get("expr")), ((Val) vit.get("message"))._UNPACK_());

        if (expr == null)
            throw new ExprVitCompilationException(vit);
        throw new ExprVitCompilationException(expr);
    }
}
