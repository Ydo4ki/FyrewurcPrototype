package org.fw.core.lib;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.base.ValsFw;
import org.fw.core.base.context.RtEnv;
import org.fw.core.lib.constraint.ConstraintFw;
import org.fw.core.lib.expr.SyntaxResolveFw;
import org.fw.core.state.obj.State;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.Expr;
import org.fw.core.base.context.Context;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.state.operation.Operation;
import org.fw.core.state.operation.OperationFw;
import org.fw.core.vit.*;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

public final class VitFw {

    public static final Type vitVal = telephonist("VitVal", (arg0, context0)
            -> FwUtils.handleSymbols(arg0, VitFw.vitVal, context0, (instance, symbol) -> {
        switch (symbol) {
            case "val":
                return instance._unpack(VitVal.class).val();
            default:
                return null;
        }
    }, (arg1, context1) -> {
        if (arg1.equals(symbol("constructor"))) {
            return FW.telephonist(() -> "VitVal.constructor", (arg, c) -> {
                return wrap(Vit.val(arg));
            });
        }
        return null;
    })).asType();

    public static final Type vitInvoke = telephonist("VitInvoke", (arg0, context0)
            -> FwUtils.handleSymbols(arg0, VitFw.vitInvoke, context0, (instance, symbol) -> {
        switch (symbol) {
            case "operation":
                return VitFw.wrap(instance._unpack(VitInvoke.class).operation());
            default:
                return null;
        }
    }, (arg1, context1) -> {
        if (arg1.equals(symbol("constructor"))) {
            return FW.telephonist(() -> "VitInvoke.constructor", (arg, ctx) -> {
                if (!VitFw.isVit(arg.type()))
                    return null;

                Vit operation = arg._unpack();
                operation = Vit.simplify(operation);
                return wrap(Vit.invoke(operation));
            });
        }
        return null;
    })).asType();

    public static final Type vitVar = telephonist("VitVar", (arg0, context0)
            -> FwUtils.handleSymbols(arg0, VitFw.vitVar, context0, (instance, symbol) -> {
        switch (symbol) {
//        case "key":
//            return ((VitVar) instance._unpack()).key();
            default:
                return null;
        }
    }, (arg1, context1) -> {
        if (arg1.equals(symbol("instance"))) {
            return wrap(Vit.var);
        }
        return null;
    })).asType();

    private static final Expr repr = FwUtils.parse("VitCall.builder").getExpr();
    public static final Type vitCall = telephonist("VitCall", (arg0, context0)
            -> FwUtils.handleSymbols(arg0, VitFw.vitCall, context0, (instance, symbol) -> {
        switch (symbol) {
            case "func":
                return wrap(((VitCall) instance._unpack()).func());
            case "arg":
                return wrap(((VitCall) instance._unpack()).arg());
            default:
                return null;
        }
    }, (arg1, context1) -> {
        if (arg1.equals(symbol("builder"))) {
            return telephonist(repr, (func, context) -> {
                if (!isVit(func.type())) {
                    return null;
                }

                return telephonist(() -> "(call " + repr + " " + func.toExpr(context) + ")", (arg, context2) -> {
                    if (!isVit(arg.type())) {
                        return null;
                    }
                    try {
                        return wrap(Vit.call(unwrap(func), unwrap(arg)));
                    } catch (VitCompilationException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
        }
        return null;
    })).asType();

    @Deprecated // this just converts it to operation, it does not evaluate anything (which leaves no room for local evaluations)
    public static final Val eval = telephonist("eval", (arg, context) -> {
        if (isVit(arg.type())) {
            Vit vit = arg._unpack();
            return telephonist((env, context1) -> {
                return State.performAndDie(scope ->
                        OperationFw.wrap(Operation.vit(Vit.reduce(vit, new Context(RtEnv.of(env), scope)), RtEnv.of(env))));
//                return ;
            });
        }
        return null;
    });
    public static final Val evalVit = telephonist("eval-vit", (arg, context) -> {
        if (isVit(arg.type())) {
            Vit vit = arg._unpack();
            return telephonist((env, context1) -> {
                return State.performAndDie(scope ->
                        vit.eval(new Context(RtEnv.of(env), scope)));
            });
        }
        return null;
    });

    public static final Val simplify = FW.telephonist("vit-simplify", (arg, context1) -> {
        if (VitFw.isVit(arg.type())) {
            return VitFw.wrap(Vit.simplify(arg._unpack()));
        }
        return null;
    });

    public static final Val reduce = FW.telephonist("vit-reduce", (arg, context1) -> {
        if (VitFw.isVit(arg.type())) {
            return FW.telephonist(() -> "(call vit-reduce " + arg.toExpr(context1) + ")", (env, context2)
                    -> State.performAndDie(
                    scope -> VitFw.wrap(Vit.reduce(arg._unpack(), new Context(RtEnv.of(env), scope))))); // thx java
        }
        return null;
    });

    public static final Val constraint = ConstraintFw.constraint(
            Vit.val(BoolFw._true),
            FwUtils.equals(
                    Vit.val(ValsFw.typeGet).call(Vit.var),
                    Vit.val(VitFw.vitVal.asVal())
            ).call(symbol("or")).call(
                    FwUtils.equals(
                            Vit.val(ValsFw.typeGet).call(Vit.var),
                            Vit.val(VitFw.vitVar.asVal())
                    )
            ).call(symbol("or")).call(
                    FwUtils.equals(
                            Vit.val(ValsFw.typeGet).call(Vit.var),
                            Vit.val(VitFw.vitCall.asVal())
                    )
            ).call(symbol("or")).call(
                    FwUtils.equals(
                            Vit.val(ValsFw.typeGet).call(Vit.var),
                            Vit.val(VitFw.vitInvoke.asVal())
                    )
            )
    );

    public static boolean isVit(Type type) {
        return type.equals(vitVal) || type.equals(vitVar) || type.equals(vitCall) || type.equals(vitInvoke);
    }

    private static final Val vitVarVal = Val.of(vitVar, Vit.var);

    public static Val wrap(Vit vit) {
        if (vit instanceof VitCall) {
            return Val.of(vitCall, vit);
        }

        if (vit instanceof VitVal) {
            return Val.of(vitVal, vit);
        }

        if (vit instanceof VitVar) {
            //noinspection ConstantValue
            if (vitVarVal == null) throw new IllegalStateException();
            return vitVarVal;
        }

        if (vit instanceof VitInvoke) {
            return Val.of(vitInvoke, vit);
        }

        throw new IllegalStateException("Unknown Vit implementation: " + vit.getClass());
    }

    public static Vit unwrap(Val vit) throws VitCompilationException {
        if (
                vit.type().equals(vitVal)
                        || vit.type().equals(vitVar)
                        || vit.type().equals(vitCall)
                        || vit.type().equals(vitInvoke)
        ) {
            return vit._unpack();
        }
        throw new VitCompilationException(vit);
    }

    @Deprecated // nafiga you did this, _unpack exists, its just one function spodifoisdfoiusiodfuiu
    public static Vit unwrap0(Val vit) {
        if (
                vit.type().equals(vitVal)
                        || vit.type().equals(vitVar)
                        || vit.type().equals(vitCall)
                        || vit.type().equals(vitInvoke)
        ) {
            return vit._unpack();
        }
        return null;
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
                    case "var": {
                        if (isize != 1) {
                            return null;
                        }
                        return VitFw.wrap(Vit.var);
                    }
                    case "call": {
                        if (isize == 1) {
                            return null;
                        }
                        Val retVit = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(compEnv)), context);
                        if (!VitFw.isVit(retVit.type()))
                            return retVit; // compile error idk

                        for (int i = 1; i < (isize - 1); i++) {
                            Val argNVit = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(i + 1), context)._unpack(), CompEnv.of(compEnv)), context);
                            if (!VitFw.isVit(argNVit.type()))
                                return argNVit; // compile error idk

                            try {
                                retVit = VitFw.wrap(VitFw.unwrap(retVit).call(VitFw.unwrap(argNVit)));
                            } catch (VitCompilationException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        return retVit;
                    }
                    case "invoke!": {
                        if (isize != 2) {
                            return null;
                        }

                        Val retVit = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(compEnv)), context);
                        if (!VitFw.isVit(retVit.type()))
                            return retVit; // compile error idk

                        Vit vit = null;
                        try {
                            vit = Vit.simplify(VitFw.unwrap(retVit));
                        } catch (VitCompilationException e) {
                            throw new RuntimeException(e);
                        }

                        return VitFw.wrap(Vit.invoke(vit));
                    }
                    case "compile-vit": {
                        if (isize != 2)
                            return null;

                        return VitFw.wrap(Vit.val(
                                compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(compEnv)), context)
                        ));
                    }
                }
            }
        }
        return null;
    }));


    public static CompEnv exports = CompEnv.of(CompEnv.compEnv(Context.outOf,
            ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                    DeclaredFw.declared(symbol("VitVal"), VitFw.vitVal.asVal()),
                    DeclaredFw.declared(symbol("VitVar"), VitFw.vitVar.asVal()),
                    DeclaredFw.declared(symbol("VitCall"), VitFw.vitCall.asVal()),
                    DeclaredFw.declared(symbol("VitInvoke"), VitFw.vitInvoke.asVal()),
                    DeclaredFw.declared(symbol("eval-vit"), VitFw.evalVit)
            )),
            VitFw.directivesCenv.asVal()
    ));
}
