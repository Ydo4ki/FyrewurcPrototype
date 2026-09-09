package org.fw.esast.expr.forstd;

import com.ydo4ki.esast.BracketsTypes;
import com.ydo4ki.esast.Expr;
import com.ydo4ki.esast.ExprList;
import com.ydo4ki.esast.Symbol;
import com.ydo4ki.fw.internal.lib.ConstraintFw;
import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import org.fw.base.EqFw;
import org.fw.base.SymbolFw;
import org.fw.base.Val;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import org.fw.core.vit.Vit;
import org.fw.esast.expr.*;
import org.fw.esast.util.FwUtils3;
import org.fw.std.DeclaredFw;
import org.fw.std.FunctionFw;
import org.fw.std.ModuleFw;
import org.fw.std.VitFw;
import org.fw.std.state.OperationLibFw;

import java.util.ArrayList;
import java.util.List;

import static org.fw.core.FW.symbol;

public final class FunctionLib {
    public static final CompEnv directivesCenv = CompEnv.of(FW.lambda_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = (Val) arg.call(FW.symbol("expr"));
            Val compEnv = (Val) arg.call(FW.symbol("comp-env"));
            Expr expr = ExprFw.unwrap(exprVal);
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case "fn": {
                        if (isize != 4)
                            return VitErrorFw.rrror(expr, "4 arguments expected");
                        Val val = ((Val) exprVal.call(DIntFw.dint(2)));
                        Expr arrow = ExprFw.unwrap(val);
                        boolean pure;
                        if (arrow instanceof Symbol) {
                            if (((Symbol) arrow).getValue().equals("!>")) pure = false;
                            else if (((Symbol) arrow).getValue().equals("->")) pure = true;
                            else return null;
                        } else return null;

                        Expr paramsE = ((Val) exprVal.call(DIntFw.dint(1)))._UNPACK_();
                        if (!(paramsE instanceof ExprList)) {
                            return VitErrorFw.rrror(paramsE, "ExprList expected");
                        }
                        if (((ExprList) paramsE).getBracketsType() != BracketsTypes.square) {
                            return VitErrorFw.rrror(paramsE, "Squared bracket ExprList expected");
                        }
                        ExprList params = ((ExprList) paramsE);
                        List<FnParam> paramsList = new ArrayList<>();
                        for (Expr param : params) {
                            if (!(param instanceof ExprList)) {
                                return VitErrorFw.rrror(param, "ExprList expected");
                            }
                            if (!((ExprList) param).get(0).toString().equals("="))
                                return VitErrorFw.rrror(((ExprList) param).get(0), "'=' expected");
                            if (((ExprList) param).size() != 2)
                                return VitErrorFw.rrror(param, "2 elements expected");

                            Expr name = ((ExprList) param).get(1);
                            if (!(name instanceof Symbol))
                                return VitErrorFw.rrror(name, "Symbol expected");

                            paramsList.add(new FnParam(((Symbol) name), ConstraintFw.isSpecified));
                        }

                        Val constraint = ConstraintFw.constraint(
                                Vit.val(EqFw.eq)
                                        .call(Vit.var.call(symbol("size")))
                                        .call(Vit.val(DIntFw.dint(paramsList.size())))
                        );

                        Expr bodyE = ((Val) exprVal.call(DIntFw.dint(3)))._UNPACK_();

                        Value newCompEnv = CompEnv.compEnv(compEnv, FW.lambda_native((arg0) -> {
                            if (arg0.getType().equals(SyntaxResolveFw.syntaxResolve)) {
                                Val exprVal0 = (Val) arg0.call(FW.symbol("expr"));
                                Expr expr0 = ExprFw.unwrap(exprVal0);
                                if (expr0 instanceof Symbol) {
                                    for (FnParam param : paramsList) {
                                        Symbol name = param.name;
                                        if (((Symbol) expr0).getValue().equals(name.getValue())) {
                                            return VitFw.wrap(Vit.var.call(ExprFw.wrap(name)));
                                        }
                                    }
                                }
                            }
                            return null;
                        }));

                        Val body = (Val) newCompEnv.call(CompEnv.syntaxResolve(bodyE, CompEnv.of(newCompEnv)));
                        if (!VitFw.isVit(body.getType()))
                            return body;

                        Vit varValuesV = Vit.var.call(symbol("%"));

                        Val newRtGetter = FW.lambda_native((oldRt) -> FW.lambda_native((varValues) -> {
                            return FW.lambda_native((argSym) -> {
                                for (int i = 0; i < paramsList.size(); i++) {
                                    FnParam param = paramsList.get(i);
                                    Symbol name = param.name;
                                    if (argSym.getType().equals(SymbolFw.symbol) && ((Symbol) ExprFw.unwrap(argSym)).getValue().equals(name.getValue())) {
                                        return varValues.call(DIntFw.dint(i));
                                    }
                                }
                                return oldRt.call(argSym);
                            });
                        }));

                        body = VitFw.wrap(Vit.invoke(Vit.val(OperationLibFw._VitOperation).call(body).call(Vit.val(newRtGetter).call(Vit.var).call(varValuesV))));

                        return VitFw.wrap(Vit.val(FunctionFw.function.asVal()).call(symbol("builder"))
                                .call(constraint)
                                .call(body)
                                .call(Vit.var));
                    }
                }
            }
        }
        return null;
    }));
    public static final Lib lib = Lib.of(
            ModuleFw.module(
                    DeclaredFw.declared(symbol("Function"), FunctionFw.function.asVal())
            ),
            directivesCenv.asValue()
    );

    static class FnParam {
        final Symbol name;
        final Val constraint;

        FnParam(Symbol name, Val constraint) {
            this.name = name;
            this.constraint = constraint;
        }
    }
}
