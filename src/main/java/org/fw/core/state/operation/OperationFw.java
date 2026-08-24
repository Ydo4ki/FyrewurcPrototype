package org.fw.core.state.operation;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.base.*;
import org.fw.core.base.context.RtEnv;
import org.fw.lib.elib.*;
import org.fw.lib.elib.expr.CompEnv;
import org.fw.lib.elib.expr.SyntaxResolveFw;
import org.fw.lib.elib.state.IfOperation;
import org.fw.lib.elib.state.WhileOperation;
import org.fw.core.state.LaserPointerFw;
import org.fw.core.state.WidePointer;
import org.fw.core.vit.Vit;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

public final class OperationFw {

    public static final Type operation = FW.telephonist("Operation", (arg) -> {
        return null;
    }).asType();

    public static final Val _VitOperation = FW.telephonist((arg) -> {
        if (!VitFw.isVit(arg.type()))
            return null;

        Vit vit = arg._unpack();

        return FW.telephonist((rtEnv) -> Operation.vit(vit, RtEnv.of(rtEnv)).asVal());
    });
    public static Val wrap(Operation operation) {
        if (operation == null) return null;
        return operation.asVal();
    }

    public static Operation unwrap(Val operation) {
        if (operation.type() == OperationFw.operation)
            return operation._unpack(Operation.class);
        return null;
    }


    public static final Val directivesCenv = FW.telephonist((arg) -> {
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
    });

    public static final Lib lib = Lib.of(
            ModuleFw.module(
                    DeclaredFw.declared(symbol("LaserPointer"), LaserPointerFw.laserPointer),
                    DeclaredFw.declared(symbol("_While"), WhileOperation._While),
                    DeclaredFw.declared(symbol("_If"), IfOperation._If),
                    DeclaredFw.declared(symbol("_CreateNewArrayOperation"), WidePointer._CreateNewArrayOperation),
//                    DeclaredFw.declared(symbol("_CreateNewObjectOperation"), LaserPointerFw._CreateNewObjectOperation),
//                    DeclaredFw.declared(symbol("_ReadOperation"), LaserPointerFw._ReadOperation),
//                    DeclaredFw.declared(symbol("_WriteOperation"), LaserPointerFw._WriteOperation),
                    DeclaredFw.declared(symbol("_VitOperation"), OperationFw._VitOperation),
                    DeclaredFw.declared(symbol("unit"), Operation.unit)
            ),
            directivesCenv
    );
}
