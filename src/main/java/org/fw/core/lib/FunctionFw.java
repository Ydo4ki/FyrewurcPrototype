package org.fw.core.lib;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.base.*;
import org.fw.core.base.context.Context;
import org.fw.core.lib.constraint.ConstraintFw;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.ExprFw;
import org.fw.core.lib.expr.SyntaxResolveFw;
import org.fw.core.state.operation.OperationFw;
import org.fw.core.util.FwUtils;
import org.fw.core.vit.*;

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

    public static final Type function = FW.telephonist((arg, context) -> {
        Val ret = function_struct.asVal().call(arg, context);
        if (arg.type().equals(SymbolFw.symbol)) {
            String value = arg._unpack(Symbol.class).getValue();
            switch (value) {
                case "builder":
                    return builderWrapper(ret);
            }
        }
        if (FwUtils.isTypeApiCall(arg, FunctionFw.function, context)) {
            Val instance = Call.getVal(arg, context);
            Val cArg = Call.getArg(arg, context);

            Val value = instance._unpack();
            if (cArg.type().equals(SymbolFw.symbol)) {
                switch (cArg._unpack(Symbol.class).getValue()) {
                    case "fn-call":
                        Val constraint = value.call(symbol("arg-constraint"), context);
                        Vit body = value.call(symbol("body"), context)._unpack();
                        return FW.telephonist((arg1, context1) -> {
                            boolean qualifies = constraint.call(symbol("check"), context1).call(arg1, context) == BoolFw._true;
                            if (!qualifies) {
                                return null;
                            }

                            // this is questionable
                            Val oldRtEnv = value.call(symbol("rt-env"), context);
//                            Val newRtEnv = FW.telephonist((arg2, context2) -> {
//                                Val ret0 = arg1.call(arg2, context2);
//                                if (Unspecified.isUnspecified(ret0)) return oldRtEnv.call(arg2, context2);
//                                return ret0;
//                            });
                            Val newRtEnv = FW.telephonist((arg2, context2) -> {
                                if (arg2.equals(symbol("%"))) return arg1;
                                else return oldRtEnv.call(arg2, context2);
                            });
                            return OperationFw._VitOperation
                                    .call(VitFw.wrap(body), context)
//                                    .call(arg1, context);
                                    .call(newRtEnv, context);
                        });
                }
            }
        }
        return ret;
    }).asType();

    public static final CompEnv directivesCenv = CompEnv.of(telephonist((arg, context) -> {
        if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"), context);
            Val compEnv = arg.call(symbol("comp-env"), context);
            Expr expr = exprVal._unpack();
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case "fn": {
                        if (isize != 4)
                            return null;
                        Expr arrow = exprVal.call(DIntFw.dint(2), context)._unpack();
                        boolean pure;
                        if (arrow instanceof Symbol) {
                            if (((Symbol) arrow).getValue().equals("!>")) pure = false;
                            else if (((Symbol) arrow).getValue().equals("->")) pure = true;
                            else return null;
                        } else return null;

                        Expr paramsE = exprVal.call(DIntFw.dint(1), context)._unpack();
                        if (!(paramsE instanceof ExprList) || ((ExprList) paramsE).getBracketsType() != BracketsTypes.square) {
                            return null;
                        }
                        ExprList params = ((ExprList) paramsE);
                        List<FnParam> paramsList = new ArrayList<>();
                        for (Expr param : params) {
                            if (!(param instanceof ExprList) || !((ExprList) param).get(0).toString().equals("="))
                                return null;
                            if (((ExprList) param).size() != 2)
                                return null;

                            Expr name = ((ExprList) param).get(1);
                            if (!(name instanceof Symbol))
                                return null;

                            paramsList.add(new FnParam(((Symbol) name), null));
                        }

                        Val constraint = ConstraintFw.constraint(
                                Vit.var.call(symbol("size")),
                                Vit.val(DIntFw.dint(paramsList.size()))
                        );

                        Expr bodyE = exprVal.call(DIntFw.dint(3), context)._unpack();

                        Val newCompEnv = CompEnv.compEnv(context, compEnv, FW.telephonist((arg0, context1) -> {
                            if (arg0.type().equals(SyntaxResolveFw.syntaxResolve)) {
                                Val exprVal0 = arg0.call(symbol("expr"), context);
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

                        Val body = newCompEnv.call(CompEnv.syntaxResolve(bodyE, CompEnv.of(newCompEnv)), context);

                        Vit varValuesV = Vit.var.call(symbol("%"));

                        Val newRtGetter = FW.telephonist((oldRt, context1) -> FW.telephonist((varValues, context3) -> {
                            return FW.telephonist((argSym, context2) -> {
                                for (int i = 0; i < paramsList.size(); i++) {
                                    FnParam param = paramsList.get(i);
                                    Symbol name = param.name;
                                    if (argSym.type().equals(SymbolFw.symbol) && argSym._unpack(Symbol.class).getValue().equals(name.getValue())) {
                                        return varValues.call(DIntFw.dint(i), context2);
                                    }
                                }
                                return oldRt.call(argSym, context2);
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

    public static CompEnv exports = CompEnv.of(CompEnv.compEnv(Context.outOf,
            ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                    DeclaredFw.declared(symbol("Function"), FunctionFw.function.asVal())
            )),
            directivesCenv.asVal()
    ));

    private static Val builderWrapper(Val builder) {
        return FW.telephonist((arg, context) -> {
            Val ret = builder.call(arg, context);
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
