package org.fw.lib.stdlib;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.base.TypeGetFw;
import org.fw.core.base.context.RtEnv;
import org.fw.lib.stdlib.expr.*;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.Expr;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.vit.*;

import java.util.ArrayList;
import java.util.List;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

public final class VitFw {

    public static final Type vitVal = FW.telephonist("VitVal", (arg0)
            -> FwUtils.handleSymbols(arg0, VitFw.vitVal, (instance, symbol) -> {
        switch (symbol) {
            case "val":
                return instance._unpack(VitVal.class).val();
            default:
                return null;
        }
    }, (arg1) -> {
        if (arg1.equals(symbol("construct"))) {
            return FW.telephonist(() -> "VitVal.construct", (arg) -> {
                return wrap(Vit.val(arg));
            });
        }
        return null;
    })).asType();

    public static final Type vitInvoke = FW.telephonist("VitInvoke", (arg0)
            -> FwUtils.handleSymbols(arg0, VitFw.vitInvoke, (instance, symbol) -> {
        switch (symbol) {
            case "operation":
                return VitFw.wrap(instance._unpack(VitInvoke.class).operation());
            default:
                return null;
        }
    }, (arg1) -> {
        if (arg1.equals(symbol("construct"))) {
            return FW.telephonist(() -> "VitInvoke.construct", (arg) -> {
                if (!VitFw.isVit(arg.type()))
                    return null;

                Vit operation = arg._unpack();
                operation = VitUtils.simplify(operation);
                return wrap(Vit.invoke(operation));
            });
        }
        return null;
    })).asType();

    public static final Type vitVar = FW.telephonist("VitVar", (arg0)
            -> FwUtils.handleSymbols(arg0, VitFw.vitVar, (instance, symbol) -> {
        switch (symbol) {
//        case "key":
//            return ((VitVar) instance._unpack()).key();
            default:
                return null;
        }
    }, (arg1) -> {
        if (arg1.equals(symbol("instance"))) {
            return wrap(Vit.var);
        }
        return null;
    })).asType();

    private static final Expr repr = FwUtils.parse("VitCall.builder").getExpr();
    public static final Type vitCall = FW.telephonist("VitCall", (arg0)
            -> FwUtils.handleSymbols(arg0, VitFw.vitCall, (instance, symbol) -> {
        switch (symbol) {
            case "func":
                return wrap(((VitCall) instance._unpack()).func());
            case "arg":
                return wrap(((VitCall) instance._unpack()).arg());
            default:
                return null;
        }
    }, (arg1) -> {
        if (arg1.equals(symbol("builder"))) {
            return telephonist(repr, (func) -> {
                if (!isVit(func.type())) {
                    return null;
                }

                return FW.telephonist((arg) -> {
                    if (!isVit(arg.type())) {
                        return null;
                    }
                    try {
                        return wrap(Vit.call(unwrap(func, null), unwrap(arg, null)));
                    } catch (VitCompilationException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
        }
        return null;
    })).asType();

    public static final CompEnv vit2exprCenv = CompEnv.of(FW.telephonist((arg) -> {
        if (arg.type().equals(SyntaxResolveFw.toExprResolve)) {
            CompEnv compEnv = CompEnv.of(arg.get("chain"));
            arg = arg.get("passing");

            Type type = arg.type();
            if (type.equals(vitVal)) {
                VitVal vitVal = arg._unpack();
                return ExprFw.wrap(ExprList.of(BracketsTypes.round, type.asVal().toExpr(compEnv), vitVal.val().toExpr(compEnv)));
            } else if (type.equals(vitVar)) {
                return ExprFw.wrap(ExprList.of(BracketsTypes.round, type.asVal().toExpr(compEnv)));
            } else if (type.equals(vitCall)) {
                VitCall vitVal = arg._unpack();
                List<Expr> elements = new ArrayList<>();
                elements.add(type.asVal().toExpr(compEnv));
                elements.addAll(vitVal.exprs(compEnv));

                return ExprFw.wrap(ExprList.of(BracketsTypes.round, elements));
            } else if (type.equals(vitInvoke)) {
                VitInvoke vitInvoke = arg._unpack();
                return ExprFw.wrap(ExprList.of(BracketsTypes.round, type.asVal().toExpr(compEnv), wrap(vitInvoke.operation()).toExpr(compEnv)));
            }
            return null;
        }
        return null;
    }));

    public static final Val evalVit = FW.telephonist("eval-vit", (arg) -> {
        if (isVit(arg.type())) {
            Vit vit = arg._unpack();
            return vit.asLambdaVal();
        }
        return null;
    });

    public static final Val simplify = FW.telephonist("vit-simplify", (arg) -> {
        if (VitFw.isVit(arg.type())) {
            return VitFw.wrap(VitUtils.simplify(arg._unpack()));
        }
        return null;
    });

    public static final Val reduce = FW.telephonist("vit-reduce", (arg) -> {
        if (VitFw.isVit(arg.type())) {
            return FW.telephonist(env
                    -> VitFw.wrap(VitUtils.reduce(arg._unpack(), RtEnv.of(env)))); // thx java
        }
        return null;
    });

    public static final Val isVit = ConstraintFw.constraint(
            FwUtils.equals(
                    Vit.val(TypeGetFw.typeGet).call(Vit.var),
                    Vit.val(VitFw.vitVal.asVal())
            ).call(symbol("or")).call(
                    FwUtils.equals(
                            Vit.val(TypeGetFw.typeGet).call(Vit.var),
                            Vit.val(VitFw.vitVar.asVal())
                    )
            ).call(symbol("or")).call(
                    FwUtils.equals(
                            Vit.val(TypeGetFw.typeGet).call(Vit.var),
                            Vit.val(VitFw.vitCall.asVal())
                    )
            ).call(symbol("or")).call(
                    FwUtils.equals(
                            Vit.val(TypeGetFw.typeGet).call(Vit.var),
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

    public static Vit unwrap(Val vit, Expr expr) throws VitCompilationException {
        if (
                vit.type().equals(vitVal)
                        || vit.type().equals(vitVar)
                        || vit.type().equals(vitCall)
                        || vit.type().equals(vitInvoke)
        ) {
            return vit._unpack();
        }
        if (vit.type().equals(VitErrorFw.vitError))
            //noinspection DataFlowIssue
            throw new VitCompilationException(ExprFw.unwrap(vit.get("expr")), vit.get("message")._unpack());

        if (expr == null)
            throw new VitCompilationException(vit);
        throw new VitCompilationException(expr);
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


    public static final CompEnv directivesCenv = CompEnv.of(FW.telephonist((arg) -> {
        if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"));
            Val compEnv = arg.call(symbol("comp-env"));
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
                        Expr eee = exprVal.call(DIntFw.dint(1))._unpack();
                        Val retVit = compEnv.call(CompEnv.syntaxResolve(eee, CompEnv.of(compEnv)));
                        if (!VitFw.isVit(retVit.type()))
                            return retVit; // compile error idk

                        for (int i = 1; i < (isize - 1); i++) {
                            Expr eeeN = exprVal.call(DIntFw.dint(i + 1))._unpack();
                            Val argNVit = compEnv.call(CompEnv.syntaxResolve(eeeN, CompEnv.of(compEnv)));
                            if (!VitFw.isVit(argNVit.type()))
                                return argNVit; // compile error idk

                            try {
                                retVit = VitFw.wrap(VitFw.unwrap(retVit, eee).call(VitFw.unwrap(argNVit, eeeN)));
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

                        Val retVit = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1))._unpack(), CompEnv.of(compEnv)));
                        if (!VitFw.isVit(retVit.type()))
                            return retVit; // compile error idk

                        Vit vit = VitUtils.simplify(retVit._unpack());

                        return VitFw.wrap(Vit.invoke(vit));
                    }
                    case "compile-vit": {
                        if (isize != 2)
                            return null;

                        return VitFw.wrap(VitUtils.simplify(Vit.val(
                                compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1))._unpack(), CompEnv.of(compEnv)))
                        )));
                    }
                    case "compile-vit-fast": {
                        if (isize != 2)
                            return null;

                        return VitFw.wrap(Vit.val(
                                compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1))._unpack(), CompEnv.of(compEnv)))
                        ));
                    }
                }
            }
        }
        return null;
    }));

    public static final Lib lib = Lib.of(
            ModuleFw.module(
                    DeclaredFw.declared(symbol("VitVal"), VitFw.vitVal.asVal()),
                    DeclaredFw.declared(symbol("VitVar"), VitFw.vitVar.asVal()),
                    DeclaredFw.declared(symbol("VitCall"), VitFw.vitCall.asVal()),
                    DeclaredFw.declared(symbol("VitInvoke"), VitFw.vitInvoke.asVal()),
                    DeclaredFw.declared(symbol("eval-vit"), VitFw.evalVit)
            ),
            CompEnv.compEnv(
                    vit2exprCenv.asVal(),
                    VitFw.directivesCenv.asVal()
            )
    );
}
