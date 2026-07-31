package org.fw.core.cases;

import org.fw.core.FW;
import org.fw.core.ast.*;
import org.fw.core.ast.lexer.ExprOutput;
import org.fw.core.base.context.Context;
import org.fw.core.base.Val;
import org.fw.core.lib.*;
import org.fw.core.lib.expr.*;
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

        State state = SystemOperation.systemState;
        Context context = new Context(rtEnv, state);
        CompEnv compEnv;
        compEnv = CompEnv.of(CompEnv.compEnv(context,
                BaseFw.exports.asVal(),
                FunctionFw.fnCallCenv.asVal(),
                VitFw.exports.asVal(),
                ExprGetFw.getterCEnv,
                DIntFw.exports.asVal(),
                ExprFw.exports.asVal(),
                StrFw.exports.asVal(),
                DVecFw.exports.asVal(),
                BoolLib.lib.exports(),
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
                                return null;

                            return new SystemOperation.ThreadSleepOperation(DIntFw.unwrap(arg).longValue()).asVal();
                        })),
                        DeclaredFw.declared(symbol("_While"), telephonist((condition, context1) -> {
                            if (condition.type() != OperationFw.operation)
                                return null;

                            return telephonist((body, context2) -> {
                                if (body.type() != OperationFw.operation)
                                    return null;

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
                    vit = compEnv.compile(expression, context);
                } catch (VitCompilationException e) {
                    System.err.println(expression);
                    throw new RuntimeException(e);
                }
                Val val = vit.eval(context);
                compEnv = CompEnv.of(CompEnv.compEnv(context,
                        compEnv.asVal(),
                        ModuleFw.ModuleCEnvFw.compEnv(val)
                ));
            }
        }

        for (LocatedExpr<? extends Expr> locatedExpression : expressions) {
            Expr expression = locatedExpression.getExpr();
            Vit vit;
            try {
                vit = compEnv.compile(expression, context);
            } catch (VitCompilationException e) {
                System.err.println(expression);
                throw new RuntimeException(e);
            }
            Val val = vit.eval(context);
            System.out.println(val.toExpr(context));
        }
    }

    public static final CompEnv directivesCenv = CompEnv.of(telephonist((arg, context) -> {
        if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"), context);
            Val compEnv = arg.call(symbol("comp-env"), context);
            Expr expr = exprVal._unpack();
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case "operation": {
                        if (isize != 2)
                            return null;


                        Val val = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(compEnv)), context);
                        if (!VitFw.isVit(val.type()))
                            return null;

                        return VitFw.wrap(Vit.val(OperationFw._VitOperation).call(val).call(Vit.var));
//                        Vit vit = VitFw.unwrap(
//                                compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(compEnv)), context)
//                        );
                    }
                }
            }
        }
        return null;
    }));

}
