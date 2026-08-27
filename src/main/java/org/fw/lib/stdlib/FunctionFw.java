package org.fw.lib.stdlib;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.base.*;
import org.fw.lib.stdlib.expr.CompEnv;
import org.fw.lib.stdlib.expr.ExprFw;
import org.fw.lib.stdlib.expr.SyntaxResolveFw;
import org.fw.core.state.operation.OperationFw;
import org.fw.core.util.FwUtils;
import org.fw.core.vit.*;
import org.fw.lib.stdlib.expr.VitErrorFw;

import java.util.ArrayList;
import java.util.List;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

public final class FunctionFw {
    public static final Type function_struct = StructFw.struct(
            DeclarationFw.declaration(symbol("arg-constraint"), ConstraintFw.toConstraint(ConstraintFw.constraint)),
            DeclarationFw.declaration(symbol("body"), VitFw.isVit),
            DeclarationFw.declaration(symbol("rt-env"), ConstraintFw.free)
    );

    public static final Type function = FW.telephonist((arg) -> {
        Val ret = function_struct.asVal().call(arg);
        if (arg.type().equals(SymbolFw.symbol)) {
            String value = arg._unpack(Symbol.class).getValue();
            switch (value) {
                case "builder":
                    return builderWrapper(ret);
            }
        }
        if (FwUtils.isTypeApiCall(arg, FunctionFw.function)) {
            Val instance = CallFw.getVal(arg);
            Val cArg = CallFw.getArg(arg);

            Val value = instance._unpack();
            if (cArg.type().equals(SymbolFw.symbol)) {
                switch (cArg._unpack(Symbol.class).getValue()) {
                    case "fn-call":
                        Val constraint = value.call(symbol("arg-constraint"));
                        Vit body = value.call(symbol("body"))._unpack();
                        return FW.telephonist((arg1) -> {
                            boolean qualifies = constraint.call(symbol("check")).call(arg1) == BoolFw._true;
                            if (!qualifies) {
                                return null;
                            }

                            // this is questionable
                            Val oldRtEnv = value.call(symbol("rt-env"));
//                            Val newRtEnv = FW.telephonist((arg2, context2) -> {
//                                Val ret0 = arg1.call(arg2, context2);
//                                if (Unspecified.isUnspecified(ret0)) return oldRtEnv.call(arg2, context2);
//                                return ret0;
//                            });
                            Val newRtEnv = FW.telephonist((arg2) -> {
                                if (arg2.equals(symbol("%"))) return arg1;
                                if (arg2.equals(symbol("%self%"))) return instance;
                                else return oldRtEnv.call(arg2);
                            });
                            return OperationFw._VitOperation
                                    .call(VitFw.wrap(body))
//                                    .call(arg1, context);
                                    .call(newRtEnv);
                        });
                }
            }
        }
        return ret;
    }).asType();

    public static final CompEnv directivesCenv = CompEnv.of(FW.telephonist((arg) -> {
        if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"));
            Val compEnv = arg.call(symbol("comp-env"));
            Expr expr = exprVal._unpack();
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case "fn": {
                        if (isize != 4)
                            return VitErrorFw.rrror(expr, "4 arguments expected");
                        Expr arrow = exprVal.call(DIntFw.dint(2))._unpack();
                        boolean pure;
                        if (arrow instanceof Symbol) {
                            if (((Symbol) arrow).getValue().equals("!>")) pure = false;
                            else if (((Symbol) arrow).getValue().equals("->")) pure = true;
                            else return null;
                        } else return null;

                        Expr paramsE = exprVal.call(DIntFw.dint(1))._unpack();
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
                                return VitErrorFw.rrror(name, "Symbol expected");;

                            paramsList.add(new FnParam(((Symbol) name), null));
                        }

                        Val constraint = ConstraintFw.constraint(
                                Vit.val(EqFw.eq)
                                        .call(Vit.var.call(symbol("size")))
                                        .call(Vit.val(DIntFw.dint(paramsList.size())))
                        );

                        Expr bodyE = exprVal.call(DIntFw.dint(3))._unpack();

                        Val newCompEnv = CompEnv.compEnv(compEnv, FW.telephonist((arg0) -> {
                            if (arg0.type().equals(SyntaxResolveFw.syntaxResolve)) {
                                Val exprVal0 = arg0.call(symbol("expr"));
                                Expr expr0 = exprVal0._unpack();
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

                        Val body = newCompEnv.call(CompEnv.syntaxResolve(bodyE, CompEnv.of(newCompEnv)));
                        if (!VitFw.isVit(body.type()))
                            return body;

                        Vit varValuesV = Vit.var.call(symbol("%"));

                        Val newRtGetter = FW.telephonist((oldRt) -> FW.telephonist((varValues) -> {
                            return FW.telephonist((argSym) -> {
                                for (int i = 0; i < paramsList.size(); i++) {
                                    FnParam param = paramsList.get(i);
                                    Symbol name = param.name;
                                    if (argSym.type().equals(SymbolFw.symbol) && argSym._unpack(Symbol.class).getValue().equals(name.getValue())) {
                                        return varValues.call(DIntFw.dint(i));
                                    }
                                }
                                return oldRt.call(argSym);
                            });
                        }));

                        body = VitFw.wrap(Vit.invoke(Vit.val(OperationFw._VitOperation).call(body).call(Vit.val(newRtGetter).call(Vit.var).call(varValuesV))));

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
            directivesCenv.asVal()
    );

    private static Val builderWrapper(Val builder) {
        return FW.telephonist((arg) -> {
            Val ret = builder.call(arg);
            if (ret.type().equals(builder.type()))
                return builderWrapper(ret);
            if (ret.type() != function_struct)
                return null;
            return Val.of(function, ret); // wrap
        });
    }

    static class FnParam {
        final Symbol name;
        final Val constraint;

        FnParam(Symbol name, Val constraint) {
            this.name = name;
            this.constraint = constraint;
        }
    }
}
