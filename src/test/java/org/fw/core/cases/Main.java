package org.fw.core.cases;

import org.fw.core.FW;
import org.fw.core.ast.*;
import org.fw.core.ast.lexer.ExprOutput;
import org.fw.core.base.Val;
import org.fw.core.lib.*;
import org.fw.core.lib.expr.*;
import org.fw.core.lib.state.IfOperation;
import org.fw.core.state.operation.OperationFw;
import org.fw.core.state.obj.State;
import org.fw.core.lib.state.SystemOperation;
import org.fw.core.lib.state.WhileOperation;
import org.fw.core.base.context.RtEnv;
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
//        Iterable<LocatedExpr<? extends Expr>> expressions = ExprOutput.valueOf(FW.class.getResourceAsStream("test-naive-fibonachi.fw"));

        State state = SystemOperation.systemState;
        CompEnv compEnv;
        compEnv = CompEnv.of(CompEnv.compEnv(
                BaseFw.exports.asVal(),
                BoolLib.lib.exports(),
                VitFw.exports.asVal(),
                ExprGetFw.getterCEnv,
                DIntFw.exports.asVal(),
                ExprFw.exports.asVal(),
                StrFw.exports.asVal(),
                DVecFw.exports.asVal(),
                FnCallFw.fnCallCenv.asVal(),
                ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                        DeclaredFw.declared(symbol("_Flush"), new SystemOperation.FlushOperation(System.out).asVal()),
                        DeclaredFw.declared(symbol("_ReadLine"), new SystemOperation.ReadLineOperation(new Scanner(System.in)).asVal()),
                        DeclaredFw.declared(symbol("_Print"), telephonist((arg)
                                -> new SystemOperation.PrintOperation(System.out, arg._unpack().toString()).asVal())),
                        DeclaredFw.declared(symbol("_Sleep"), telephonist((arg) -> {
                            if (arg.type() != DIntFw.dint)
                                return null;

                            return new SystemOperation.ThreadSleepOperation(DIntFw.unwrap(arg).longValue()).asVal();
                        })),
                        DeclaredFw.declared(symbol("_While"), WhileOperation._While),
                        DeclaredFw.declared(symbol("_If"), IfOperation._If),
                        DeclaredFw.declared(symbol("_CreateNewObjectOperation"), OperationFw._CreateNewObjectOperation),
                        DeclaredFw.declared(symbol("_ReadOperation"), OperationFw._ReadOperation),
                        DeclaredFw.declared(symbol("_WriteOperation"), OperationFw._WriteOperation),
                        DeclaredFw.declared(symbol("_VitOperation"), OperationFw._VitOperation)
                )),
                ModuleFw.exports.asVal(),
                FunctionFw.exports.asVal(),
                DeclaredFw.exports.asVal(),
                CompEnvLib.exports.asVal(),
                DoFw.exports.asVal(),
                UseFw.useDirectivesCenv.asVal(),
                directivesCenv.asVal(),

                ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                        DeclaredFw.declared(symbol("test-mod"), ModuleFw.module(
                                DeclaredFw.declared(symbol("test-value"), DIntFw.dint(94))
                        ))
                ))
        ));

        List<Iterable<LocatedExpr<? extends Expr>>> additionals = new ArrayList<>();
        additionals.add(ExprOutput.valueOf(FW.class.getResourceAsStream("operationfns.fw")));

        for (Iterable<LocatedExpr<? extends Expr>> additional1 : additionals) {
            for (LocatedExpr<? extends Expr> locatedExpression : additional1) {
                Expr expression = locatedExpression.getExpr();
                Vit vit;
                try {
                    vit = compEnv.compile(expression);
                } catch (VitCompilationException e) {
                    System.err.println(expression);
                    throw new RuntimeException(e);
                }
                Val val = vit.eval(rtEnv, state);
                compEnv = CompEnv.of(CompEnv.compEnv(
                        compEnv.asVal(),
                        ModuleFw.ModuleCEnvFw.compEnv(val)
                ));
            }
        }

        for (LocatedExpr<? extends Expr> locatedExpression : expressions) {
            Expr expression = locatedExpression.getExpr();
            Vit vit;
            try {
                vit = compEnv.compile(expression);
            } catch (VitCompilationException e) {
                System.err.println(expression);
                throw new RuntimeException(e);
            }
            Val val = vit.eval(rtEnv, state);
//            System.out.println(val.toExpr(context));
        }
    }

    public static final CompEnv directivesCenv = CompEnv.of(telephonist((arg) -> {
        if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"));
            Val compEnv = arg.call(symbol("comp-env"));
            Expr expr = exprVal._unpack();
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case "operation": {
                        if (isize != 2)
                            return null;


                        Val val = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1))._unpack(), CompEnv.of(compEnv)));
                        if (!VitFw.isVit(val.type()))
                            return null;

                        return VitFw.wrap(Vit.val(OperationFw._VitOperation).call(val).call(Vit.var));
//                        Vit vit = VitFw.unwrap(
//                                compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(compEnv)), context)
//                        );
                    }
                    case "module": {
                        Vit builder = Vit.val(DVecFw.emptyBuilder);
                        for (int i = 1; i < isize; i++) {
                            Val val = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(i))._unpack(), CompEnv.of(compEnv)));
                            if (!VitFw.isVit(val.type()))
                                return null;

                            builder = builder.call(val._unpack(Vit.class));
                        }
                        builder = Vit.call(DVecFw.dvecbf, builder);

                        return VitFw.wrap(Vit.val(ModuleFw.module.asVal()).call(symbol("constructor")).call(builder));
                    }
                    case "while": {
                        if (isize != 3)
                            return null;

                        Val condition = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1))._unpack(), CompEnv.of(compEnv)));
                        Val body = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(2))._unpack(), CompEnv.of(compEnv)));

                        Vit ret = Vit.invoke(Vit.val(WhileOperation._While)
                                .call(Vit.call(OperationFw._VitOperation, condition).call(Vit.var))
                                .call(Vit.call(OperationFw._VitOperation, body).call(Vit.var))
                        );
                        return VitFw.wrap(ret);
                    }
                    case "if": {
                        if (isize != 5)
                            return null;

                        Val condition = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1))._unpack(), CompEnv.of(compEnv)));
                        Val ifTrue = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(2))._unpack(), CompEnv.of(compEnv)));
                        Expr ELSE = exprVal.call(DIntFw.dint(3))._unpack();
                        if (!ELSE.toString().equals("else"))
                            return null;

                        Val ifFalse = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(4))._unpack(), CompEnv.of(compEnv)));

                        Vit ret = Vit.invoke(Vit.val(IfOperation._If)
                                .call(Vit.call(OperationFw._VitOperation, condition).call(Vit.var))
                                .call(Vit.call(OperationFw._VitOperation, ifTrue).call(Vit.var))
                                .call(Vit.call(OperationFw._VitOperation, ifFalse).call(Vit.var))
                        );
                        return VitFw.wrap(ret);
                    }
                }
            }
        }
        return null;
    }));

}
