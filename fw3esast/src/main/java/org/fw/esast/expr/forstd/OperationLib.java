package org.fw.esast.expr.forstd;

import com.ydo4ki.esast.BracketsTypes;
import com.ydo4ki.esast.Expr;
import com.ydo4ki.esast.ExprList;
import com.ydo4ki.esast.Symbol;
import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import org.fw.base.OperationFw;
import org.fw.base.Val;
import org.fw.core.FW;
import org.fw.core.state.obj.LaserPointerFw;
import org.fw.core.state.obj.ScopeFw;
import org.fw.core.state.obj.StatePointerFw;
import org.fw.core.state.operation.Operation;
import org.fw.core.vit.Vit;
import org.fw.esast.expr.*;
import org.fw.std.DeclaredFw;
import org.fw.std.ModuleFw;
import org.fw.std.VitFw;
import org.fw.std.state.OperationLibFw;
import org.fw.std.state.operation.IfOperation;
import org.fw.std.state.operation.WhileOperation;

import static org.fw.core.FW.symbol;

public final class OperationLib {
    public static final Val directivesCenv = FW.lambda_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = (Val) arg.call(FW.symbol("expr"));
            Val compEnv = (Val) arg.call(FW.symbol("comp-env"));
            Expr expr = (Expr) ExprFw.unwrap(exprVal);
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case "operation": {
                        if (isize != 2)
                            return null;


                        Val val1 = ((Val) exprVal.call(DIntFw.dint(1)));
                        Val val = (Val) compEnv.call(CompEnv.syntaxResolve((Expr) ExprFw.unwrap(val1), CompEnv.of(compEnv)));
                        if (!VitFw.isVit(val.getType()))
                            return null;

                        return VitFw.wrap(Vit.val(OperationLibFw._VitOperation).call(val).call(Vit.var));
                    }
                    case "while": {
                        if (isize != 3)
                            return VitErrorFw.rrror(expr, "3 elements expected");

                        Val val1 = ((Val) exprVal.call(DIntFw.dint(1)));
                        Val condition = (Val) compEnv.call(CompEnv.syntaxResolve((Expr) ExprFw.unwrap(val1), CompEnv.of(compEnv)));
                        if (!VitFw.isVit(condition.getType()))
                            return condition;
                        Val val = ((Val) exprVal.call(DIntFw.dint(2)));
                        Val body = (Val) compEnv.call(CompEnv.syntaxResolve((Expr) ExprFw.unwrap(val), CompEnv.of(compEnv)));
                        if (!VitFw.isVit(body.getType()))
                            return body;

                        Vit ret = Vit.invoke(Vit.val(WhileOperation._While)
                                .call(Vit.call(OperationLibFw._VitOperation, condition).call(Vit.var))
                                .call(Vit.call(OperationLibFw._VitOperation, body).call(Vit.var))
                        );
                        return VitFw.wrap(ret);
                    }
                    case "if": {
                        if (isize != 5)
                            return null;

                        Val val3 = ((Val) exprVal.call(DIntFw.dint(1)));
                        Val condition = (Val) compEnv.call(CompEnv.syntaxResolve((Expr) ExprFw.unwrap(val3), CompEnv.of(compEnv)));
                        Val val2 = ((Val) exprVal.call(DIntFw.dint(2)));
                        Val ifTrue = (Val) compEnv.call(CompEnv.syntaxResolve((Expr) ExprFw.unwrap(val2), CompEnv.of(compEnv)));
                        Val val1 = ((Val) exprVal.call(DIntFw.dint(3)));
                        Expr ELSE = (Expr) ExprFw.unwrap(val1);
                        if (!ELSE.toString().equals("else"))
                            return null;

                        Val val = ((Val) exprVal.call(DIntFw.dint(4)));
                        Val ifFalse = (Val) compEnv.call(CompEnv.syntaxResolve((Expr) ExprFw.unwrap(val), CompEnv.of(compEnv)));

                        Vit ret = Vit.invoke(Vit.val(IfOperation._If)
                                .call(Vit.call(OperationLibFw._VitOperation, condition).call(Vit.var))
                                .call(Vit.call(OperationLibFw._VitOperation, ifTrue).call(Vit.var))
                                .call(Vit.call(OperationLibFw._VitOperation, ifFalse).call(Vit.var))
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
                    DeclaredFw.declared(symbol("StatePointer"), StatePointerFw.statePointer),
                    DeclaredFw.declared(symbol("ScopePointer"), ScopeFw.scopePointer),
                    DeclaredFw.declared(symbol("LaserPointer"), LaserPointerFw.laserPointer),
                    DeclaredFw.declared(symbol("Operation"), OperationFw.operation),
                    DeclaredFw.declared(symbol("_While"), WhileOperation._While),
                    DeclaredFw.declared(symbol("_If"), IfOperation._If),
                    DeclaredFw.declared(symbol("_VitOperation"), OperationLibFw._VitOperation),
                    DeclaredFw.declared(symbol("unit"), Operation.unit)
            ),
            directivesCenv
    );
}
