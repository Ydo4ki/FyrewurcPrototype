package org.fw.core.cases;

import org.fw.core.FW;
import org.fw.core.ast.*;
import org.fw.core.ast.lexer.ExprOutput;
import org.fw.core.base.Context;
import org.fw.core.base.Unspecified;
import org.fw.core.base.Val;
import org.fw.core.lib.*;
import org.fw.core.lib.constraint.ConstraintFw;
import org.fw.core.lib.expr.*;
import org.fw.core.lib.state.OperationFw;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.SystemOperation;
import org.fw.core.state.operation.WhileOperation;
import org.fw.core.vit.RtEnv;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

public class Main {

    public static final RtEnv rtEnv = RtEnv.of(ModuleFw.module(
            DeclaredFw.declared(symbol("to-expr"), ToExprFn.toExpr)
    ));

    public static void main(String[] args) {
        Iterable<LocatedExpr<? extends Expr>> expressions = ExprOutput.valueOf(FW.class.getResourceAsStream("test-bullsandcows.fw"));

        State state = SystemOperation.systemState;
        Context context = new Context(rtEnv, state);
        CompEnv compEnv;
        compEnv = CompEnv.of(CompEnv.compEnv(context,
                fnCallCenv.asVal(),
                VitFw.exports.asVal(),
                ExprGetFw.getterCEnv,
                DIntFw.exports.asVal(),
                ExprFw.exports.asVal(),
                StrFw.exports.asVal(),
                DVecFw.exports.asVal(),
                ValsFw.exports.asVal(),
                BoolFw.exports.asVal(),
                ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                        DeclaredFw.declared(symbol("Telephonist"), Val.ofTelephonist(0))
                )),
                ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                        DeclaredFw.declared(symbol("_Flush"), new SystemOperation.FlushOperation(System.out).asVal()),
                        DeclaredFw.declared(symbol("_ReadLine"), new SystemOperation.ReadLineOperation(new Scanner(System.in)).asVal()),
                        DeclaredFw.declared(symbol("_Print"), telephonist((arg, context1)
                                -> new SystemOperation.PrintOperation(System.out, arg._unpack().toString()).asVal())),
                        DeclaredFw.declared(symbol("_Sleep"), telephonist((arg, context1) -> {
                            if (arg.type() != DIntFw.dint)
                                return Unspecified.unspecified;

                            return new SystemOperation.ThreadSleepOperation(DIntFw.unwrap(arg).longValue()).asVal();
                        })),
                        DeclaredFw.declared(symbol("_While"), telephonist((condition, context1) -> {
                            if (condition.type() != OperationFw.operation)
                                return Unspecified.unspecified;

                            return telephonist((body, context2) -> {
                                if (body.type() != OperationFw.operation)
                                    return Unspecified.unspecified;

                                return new WhileOperation(condition._unpack(), body._unpack()).asVal();
                            });
                        })),
                        DeclaredFw.declared(symbol("_CreateNewObjectOperation"), OperationFw._CreateNewObjectOperation),
                        DeclaredFw.declared(symbol("_ReadOperation"), OperationFw._ReadOperation),
                        DeclaredFw.declared(symbol("_WriteOperation"), OperationFw._WriteOperation),
                        DeclaredFw.declared(symbol("_VitOperation"), OperationFw._VitOperation)
                )),
                ModuleFw.exports.asVal(),
                FunctionFw.exports.asVal(),
                DeclaredFw.exports.asVal(),
                ConstraintFw.exports.asVal(),
                DoFw.exports.asVal(),
                UseFw.useDirectivesCenv.asVal(),
                directivesCenv.asVal(),

                ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                        DeclaredFw.declared(symbol("test-mod"), ModuleFw.module(
                                DeclaredFw.declared(symbol("test-value"), DIntFw.dint(94))
                        ))
                ))
        ));

        for (LocatedExpr<? extends Expr> locatedExpression : expressions) {
            Expr expression = locatedExpression.getExpr();
            Vit vit;
            try {
                vit = compEnv.compile(expression, context);
            } catch (VitCompilationException e) {
                throw new RuntimeException(e);
            }
            Val val = vit.eval(context);
//            System.out.println(val.toExpr(context));
        }
    }

    public static final CompEnv fnCallCenv = CompEnv.of(telephonist((arg, context) -> {
        if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"), context);
            Val compEnv = arg.call(symbol("comp-env"), context);
            Expr expr = exprVal._unpack();
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();

                Val fvv = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(0), context)._unpack(), CompEnv.of(compEnv)), context);
                if (!VitFw.isVit(fvv.type()))
                    return Unspecified.unspecified;
                Vit fv = VitFw.unwrap(fvv);

                Vit varValuesV = Vit.val(DVecFw.emptyBuilder);
                for (int i = 1; i < isize; i++) {
                    varValuesV = varValuesV.call(VitFw.unwrap(compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(i), context)._unpack(), CompEnv.of(compEnv)), context)));
                }
                varValuesV = Vit.val(DVecFw.dvecbf).call(varValuesV);

                return VitFw.wrap(Vit.invoke(fv.call(symbol("fn-call")).call(varValuesV)));
            }
        }
        return Unspecified.unspecified;
    }));

    public static final CompEnv directivesCenv = CompEnv.of(telephonist((arg, context) -> {
        if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"), context);
            Val compEnv = arg.call(symbol("comp-env"), context);
            Expr expr = exprVal._unpack();
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case "compile-vit": {
                        if (isize != 2)
                            return Unspecified.unspecified;

                        return VitFw.wrap(Vit.val(
                                compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(compEnv)), context)
                        ));
                    }
                    case "fn": {
                        if (isize != 4)
                            return Unspecified.unspecified;
                        Expr arrow = exprVal.call(DIntFw.dint(2), context)._unpack();
                        boolean pure;
                        if (arrow instanceof Symbol) {
                            if (((Symbol) arrow).getValue().equals("!>")) pure = false;
                            else if (((Symbol) arrow).getValue().equals("->")) pure = true;
                            else return Unspecified.unspecified;
                        } else return Unspecified.unspecified;

                        Expr paramsE = exprVal.call(DIntFw.dint(1), context)._unpack();
                        if (!(paramsE instanceof ExprList) || ((ExprList) paramsE).getBracketsType() != BracketsTypes.square) {
                            return Unspecified.unspecified;
                        }
                        ExprList params = ((ExprList) paramsE);
                        List<FnParam> paramsList = new ArrayList<>();
                        for (Expr param : params) {
                            if (!(param instanceof ExprList) || !((ExprList) param).get(0).toString().equals("="))
                                return Unspecified.unspecified;
                            if (((ExprList) param).size() != 2)
                                return Unspecified.unspecified;

                            Expr name = ((ExprList) param).get(1);
                            if (!(name instanceof Symbol))
                                return Unspecified.unspecified;

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
                            return Unspecified.unspecified;
                        }));

                        Val body = newCompEnv.call(CompEnv.syntaxResolve(bodyE, CompEnv.of(newCompEnv)), context);

                        Vit varValuesV = Vit.val(DVecFw.emptyBuilder);
                        for (int i = 0; i < paramsList.size(); i++) {
                            varValuesV = varValuesV.call(Vit.var.call(DIntFw.dint(i)));
                        }
                        varValuesV = Vit.val(DVecFw.dvecbf).call(varValuesV);

                        Val newRtGetter = FW.telephonist((oldRt, context1) -> FW.telephonist((varValues, context3) -> {
                            return FW.telephonist((argSym, context2) -> {
                                for (int i = 0; i < paramsList.size(); i++) {
                                    FnParam param = paramsList.get(i);
                                    Symbol name = param.name;
                                    if (argSym.type().equals(ExprFw.symbol) && argSym._unpack(Symbol.class).getValue().equals(name.getValue())) {
                                        return varValues.call(DIntFw.dint(i), context2);
                                    }
                                }
                                return oldRt.call(argSym, context2);
                            });
                        }));

                        body = VitFw.wrap(Vit.invoke(Vit.val(OperationFw._VitOperation).call(body).call(Vit.val(newRtGetter).call(Vit.var).call(varValuesV))));

//                        System.out.println(body.toExpr(context));
                        return VitFw.wrap(Vit.val(FunctionFw.function.asVal()).call(symbol("builder"))
                                .call(constraint)
                                .call(body)
                                .call(Vit.var));
                    }
                    case "operation": {
                        if (isize != 2)
                            return Unspecified.unspecified;


                        Val val = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(compEnv)), context);
                        if (!VitFw.isVit(val.type()))
                            return Unspecified.unspecified;

                        return VitFw.wrap(Vit.val(OperationFw._VitOperation).call(val).call(Vit.var));
//                        Vit vit = VitFw.unwrap(
//                                compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(compEnv)), context)
//                        );
                    }
                }
            }
        }
        return Unspecified.unspecified;
    }));

    static class FnParam {
        final Symbol name;
        final Val constraint;

        FnParam(Symbol name, Val constraint) {
            this.name = name;
            this.constraint = constraint;
        }
    }
}
