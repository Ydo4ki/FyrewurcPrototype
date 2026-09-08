package org.fw.std;

import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import org.fw.base.*;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import org.fw.esast.expr.*;
import org.fw.esast.extern.BracketsTypes;
import org.fw.esast.extern.Expr;
import org.fw.esast.extern.ExprList;
import org.fw.esast.extern.Symbol;

import org.fw.std.state.OperationFw;
import org.fw.core.util.FwUtils;
import org.fw.core.vit.*;

import java.util.ArrayList;
import java.util.List;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist_native;

public final class FunctionFw {
    public static final Type function_struct = StructFw.struct(
            DeclarationFw.declaration(symbol("arg-constraint"), ConstraintFw.toConstraint(ConstraintFw.constraint)),
            DeclarationFw.declaration(symbol("body"), VitFw.isVit),
            DeclarationFw.declaration(symbol("rt-env"), ConstraintFw.free)
    );

    public static final Type function = FW.telephonist_native((arg) -> {
        Val val1 = function_struct.asVal();
        Val ret = (Val) val1.call(arg);
        if (arg.getType().equals(SymbolFw.symbol)) {
            String value = arg._UNPACK_(Symbol.class).getValue();
            switch (value) {
                case "builder":
                    return builderWrapper(ret);
            }
        }
        if (FwUtils.isTypeApiCall(arg, FunctionFw.function)) {
            Val instance = (Val) CallFw.getVal(arg);
            Val cArg = (Val) CallFw.getArg(arg);

            Val value = instance._UNPACK_();
            if (cArg.getType().equals(SymbolFw.symbol)) {
                switch (cArg._UNPACK_(Symbol.class).getValue()) {
                    case "fn-call":
                        Value constraint = (Val) value.get("arg-constraint");
                        Vit body = ((Val) (Val) value.get("body"))._UNPACK_();
                        return FW.telephonist_native((arg1) -> {
                            boolean qualifies = constraint.get("check").call(arg1).impliesEquality(BoolFw._true);
                            if (!qualifies) {
                                return null;
                            }

                            // this is questionable
                            Value oldRtEnv = (Val) value.get("rt-env");
//                            Val newRtEnv = FW.telephonist((arg2, context2) -> {
//                                Val ret0 = arg1.call(arg2, context2);
//                                if (Unspecified.isUnspecified(ret0)) return oldRtEnv.call(arg2, context2);
//                                return ret0;
//                            });
                            Val newRtEnv = FW.telephonist_native((arg2) -> {
                                if (arg2.equalsSymbol("%")) return arg1;
                                if (arg2.equalsSymbol("%self%")) return instance;
                                else return oldRtEnv.call(arg2);
                            });
                            //                                    .call(arg1, context);
                            Val val = ((Val) OperationFw._VitOperation.call(VitFw.wrap(body)));
                            return (Val) val.call(newRtEnv);
                        });
                }
            }
        }
        return ret;
    }).asType();

    public static final CompEnv directivesCenv = CompEnv.of(FW.telephonist_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = (Val) arg.call(FW.symbol("expr"));
            Val compEnv = (Val) arg.call(FW.symbol("comp-env"));
            Expr expr = exprVal._UNPACK_(Expr.class);
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case "fn": {
                        if (isize != 4)
                            return VitErrorFw.rrror(expr, "4 arguments expected");
                        Expr arrow = ((Val) exprVal.call(DIntFw.dint(2)))._UNPACK_(Expr.class);
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
                                return VitErrorFw.rrror(name, "Symbol expected");;

                            paramsList.add(new FnParam(((Symbol) name), null));
                        }

                        Val constraint = ConstraintFw.constraint(
                                Vit.val(EqFw.eq)
                                        .call(Vit.var.call(symbol("size")))
                                        .call(Vit.val(DIntFw.dint(paramsList.size())))
                        );

                        Expr bodyE = ((Val) exprVal.call(DIntFw.dint(3)))._UNPACK_();

                        Value newCompEnv = CompEnv.compEnv(compEnv, FW.telephonist_native((arg0) -> {
                            if (arg0.getType().equals(SyntaxResolveFw.syntaxResolve)) {
                                Val exprVal0 = (Val) arg0.call(FW.symbol("expr"));
                                Expr expr0 = exprVal0._UNPACK_(Expr.class);
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

                        Val newRtGetter = FW.telephonist_native((oldRt) -> FW.telephonist_native((varValues) -> {
                            return FW.telephonist_native((argSym) -> {
                                for (int i = 0; i < paramsList.size(); i++) {
                                    FnParam param = paramsList.get(i);
                                    Symbol name = param.name;
                                    if (argSym.getType().equals(SymbolFw.symbol) && argSym._UNPACK_(Symbol.class).getValue().equals(name.getValue())) {
                                        return (Val) varValues.call((Value) DIntFw.dint(i));
                                    }
                                }
                                return (Val) oldRt.call((Value) argSym);
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
            directivesCenv.asValue()
    );

    private static Val builderWrapper(Val builder) {
        return FW.telephonist_native((arg) -> {
            Val ret = (Val) builder.call((Value) arg);
            if (ret.getType().equals(builder.getType()))
                return builderWrapper(ret);
            if (ret.getType() != function_struct)
                return null;
            return Val._NEW_INSTANCE_(function, ret); // wrap
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
