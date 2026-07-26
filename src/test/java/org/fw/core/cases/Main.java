package org.fw.core.cases;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.ast.lexer.ExprOutput;
import org.fw.core.base.Context;
import org.fw.core.base.Val;
import org.fw.core.lib.*;
import org.fw.core.lib.constraint.ConstraintFw;
import org.fw.core.lib.expr.*;
import org.fw.core.lib.state.LaserPointerFw;
import org.fw.core.lib.state.OperationFw;
import org.fw.core.state.obj.Obj;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;
import org.fw.core.vit.RtEnv;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

public class Main {

    public static final RtEnv rtEnv = RtEnv.of(ModuleFw.module(
            DeclaredFw.declared(symbol("to-expr"), ToExprFn.toExpr)
    ));

    public static void main(String[] args) {
        Iterable<Expr> expressions = ExprOutput.valueOf(FW.class.getResourceAsStream("test.fw"));

        State state = Operation.systemState;
        Context context = new Context(rtEnv, state);
        CompEnv compEnv;
        compEnv = CompEnv.of(CompEnv.compEnv(context,
                VitFw.exports.asVal(),
                ExprGetFw.getterCEnv,
                DIntFw.exports.asVal(),
                ExprFw.exports.asVal(),
                StrFw.exports.asVal(),
                DVecFw.exports.asVal(),
                ValsFw.exports.asVal(),
                ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                        DeclaredFw.declared(symbol("Telephonist"), Val.ofTelephonist(0))
                )),
                ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                        DeclaredFw.declared(symbol("_PrintHelloWorld"), new Operation.HelloWorldOperation().asVal()),
                        DeclaredFw.declared(symbol("_CreateNewObjectOperation"), FW.telephonist((arg, context1) -> {
                            return new Operation.CreateNewObjectOperation(arg).asVal();
                        })),
                        DeclaredFw.declared(symbol("_ReadOperation"), FW.telephonist((arg, context1) -> {
                            if (arg.type() != LaserPointerFw.laserPointer)
                                return Val.unspecified;

                            Obj obj = arg._unpack();
                            if (!(obj instanceof Obj.ValObj))
                                return Val.unspecified;

                            return Operation.read((Obj.ValObj)obj).asVal();
                        })),
                        DeclaredFw.declared(symbol("_WriteOperation"), FW.telephonist((arg, context1) -> {
                            if (arg.type() != LaserPointerFw.laserPointer)
                                return Val.unspecified;

                            Obj obj = arg._unpack();
                            if (!(obj instanceof Obj.ValObj))
                                return Val.unspecified;

                            return FW.telephonist((arg1, context2) -> {
                                return Operation.write((Obj.ValObj)obj, arg1).asVal();
                            });
                        })),
                        DeclaredFw.declared(symbol("_VitOperation"), OperationFw.vitOperation)
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

        for (Expr expression : expressions) {
            Vit vit;
            try {
                vit = compEnv.compile(expression, context);
            } catch (VitCompilationException e) {
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
                    case "compile-vit": {
                        if (isize != 2)
                            return Val.unspecified;

                        return VitFw.wrap(Vit.val(
                                compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(compEnv)), context)
                        ));
                    }
                }
            }
        }
        return Val.unspecified;
    }));

}
