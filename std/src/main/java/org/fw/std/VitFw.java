package org.fw.std;

import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import com.ydo4ki.fw.internal.lib.ConstraintFw;
import org.fw.esast.ExprVitCompilationException;
import org.fw.base.*;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import com.ydo4ki.esast.BracketsTypes;
import com.ydo4ki.esast.ExprList;
import com.ydo4ki.esast.Symbol;

import org.fw.core.util.FwUtils;
import com.ydo4ki.esast.Expr;
import org.fw.core.vit.*;
import org.fw.esast.expr.*;
import org.fw.esast.util.FwUtils3;

import java.util.ArrayList;
import java.util.List;

import static org.fw.core.FW.symbol;

public final class VitFw {

    public static final Type vitVal = FW.telephonist_native("VitVal", (arg0) -> {
        if (FwUtils.isTypeApiCall(arg0, VitFw.vitVal)) {
            Val instance2 = (Val) CallFw.getVal(arg0);
            Val callArg = (Val) CallFw.getArg(arg0);
            if (!callArg.getType().equals(SymbolFw.symbol)) {
                return null;
            }
            String symbol1 = callArg._UNPACK_().toString();
            switch (symbol1) {
                case "val":
                    return instance2._UNPACK_(VitVal.class).val();
                default:
                    return null;
            }
        }
        if (arg0.equalsSymbol("construct")) {
            return FW.telephonist_native("VitVal.construct", (arg) -> wrap(Vit.val(arg)));
        }
        return null;
    }).asType();

    public static final Type vitInvoke = FW.telephonist_native("VitInvoke", (arg0) -> {
        if (FwUtils.isTypeApiCall(arg0, VitFw.vitInvoke)) {
            Val instance2 = (Val) CallFw.getVal(arg0);
            Val callArg = (Val) CallFw.getArg(arg0);
            if (!callArg.getType().equals(SymbolFw.symbol)) {
                return null;
            }
            String symbol1 = callArg._UNPACK_();
            switch (symbol1) {
                case "operation":
                    return VitFw.wrap(instance2._UNPACK_(VitInvoke.class).operation());
                default:
                    return null;
            }
        }
        if (arg0.equalsSymbol("construct")) {
            return FW.telephonist_native("VitInvoke.construct", (arg) -> {
                    if (!VitFw.isVit(arg.getType()))
                        return null;

                    Vit operation = arg._UNPACK_();
                    operation = VitUtils.simplify(operation);
                    return wrap(Vit.invoke(operation));
                });
        }
        return null;
    }).asType();

    public static final Type vitVar = FW.telephonist_native("VitVar", (arg0) -> {
        //        case "key":
//            return ((VitVar) instance._unpack()).key();
        //        case "key":
        //            return ((VitVar) instance._unpack()).key();
        if (FwUtils.isTypeApiCall(arg0, VitFw.vitVar)) {
            Val instance2 = (Val) CallFw.getVal(arg0);
            Val callArg = (Val) CallFw.getArg(arg0);
            if (!callArg.getType().equals(SymbolFw.symbol)) {
                return null;
            }
            String symbol1 = callArg._UNPACK_();
            switch (symbol1) {
//        case "key":
//            return ((VitVar) instance._unpack()).key();
                default:
                    return null;
            }
        }
        if (arg0.equalsSymbol("instance")) {
            return wrap(Vit.var);
        }
        return null;
    }).asType();

    public static final Type vitCall = FW.telephonist_native("VitCall", (arg0)
            -> {
        if (FwUtils.isTypeApiCall(arg0, VitFw.vitCall)) {
            Val instance2 = (Val) CallFw.getVal(arg0);
            Val callArg = (Val) CallFw.getArg(arg0);
            if (!callArg.getType().equals(SymbolFw.symbol)) {
                return ((FwUtils.NSHandler) (instance1, arg3) -> null).handle(instance2, callArg);
            }
            String symbol1 = callArg._UNPACK_();
            switch (symbol1) {
                case "func":
                    return wrap(((VitCall) instance2._UNPACK_()).func());
                case "arg":
                    return wrap(((VitCall) instance2._UNPACK_()).arg());
                default:
                    return null;
            }
        }
        if (arg0.equalsSymbol("builder")) {
            return FW.telephonist_native("VitCall.builder", (func) -> {
                if (!isVit(func.getType())) {
                    return null;
                }

                return FW.telephonist_native((arg) -> {
                    if (!isVit(arg.getType())) {
                        return null;
                    }
                    try {
                        return wrap(Vit.call(unwrap(func, null), unwrap(arg, null)));
                    } catch (ExprVitCompilationException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
        }
        return null;
    }).asType();

    public static final CompEnv vit2exprCenv = CompEnv.of(FW.telephonist_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.toExprResolve)) {
            CompEnv compEnv = CompEnv.of((Val) arg.get("chain"));
            arg = (Val) arg.get("passing");

            Type type = arg.getType();
            if (type.equals(vitVal)) {
                VitVal vitVal = arg._UNPACK_();
                Value value = type.asVal();
                return ExprFw.wrap(ExprList.of(BracketsTypes.round, compEnv.toExpr(value), compEnv.toExpr(vitVal.val())));
            } else if (type.equals(vitVar)) {
                Value value = type.asVal();
                return ExprFw.wrap(ExprList.of(BracketsTypes.round, compEnv.toExpr(value)));
            } else if (type.equals(vitCall)) {
                VitCall vitVal = arg._UNPACK_();
                List<Expr> elements = new ArrayList<>();
                Value value = type.asVal();
                elements.add(compEnv.toExpr(value));
                elements.addAll(FwUtils3.exprs(vitVal, compEnv));

                return ExprFw.wrap(ExprList.of(BracketsTypes.round, elements));
            } else if (type.equals(vitInvoke)) {
                VitInvoke vitInvoke = arg._UNPACK_();
                Value value = wrap(vitInvoke.operation());
                Value value1 = type.asVal();
                return ExprFw.wrap(ExprList.of(BracketsTypes.round, compEnv.toExpr(value1), compEnv.toExpr(value)));
            }
            return null;
        }
        return null;
    }));

    public static final Val evalVit = FW.telephonist_native("eval-vit", (arg) -> {
        if (isVit(arg.getType())) {
            Vit vit = arg._UNPACK_();
            return vit.asLambdaVal();
        }
        return null;
    });

    public static final Val simplify = FW.telephonist_native("vit-simplify", (arg) -> {
        if (VitFw.isVit(arg.getType())) {
            return VitFw.wrap(VitUtils.simplify(arg._UNPACK_()));
        }
        return null;
    });

    public static final Val reduce = FW.telephonist_native("vit-reduce", (arg) -> {
        if (VitFw.isVit(arg.getType())) {
            return FW.telephonist_native(env
                    -> VitFw.wrap(VitUtils.reduce(arg._UNPACK_(), env))); // thx java
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

    private static final Val vitVarVal = Val._NEW_INSTANCE_(vitVar, Vit.var);

    public static Val wrap(Vit vit) {
        if (vit instanceof VitCall) {
            return Val._NEW_INSTANCE_(vitCall, vit);
        }

        if (vit instanceof VitVal) {
            return Val._NEW_INSTANCE_(vitVal, vit);
        }

        if (vit instanceof VitVar) {
            //noinspection ConstantValue
            if (vitVarVal == null) throw new IllegalStateException();
            return vitVarVal;
        }

        if (vit instanceof VitInvoke) {
            return Val._NEW_INSTANCE_(vitInvoke, vit);
        }

        throw new IllegalStateException("Unknown Vit implementation: " + vit.getClass());
    }

    public static Vit unwrap(Val vit, Expr expr) throws ExprVitCompilationException {
        if (
                vit.getType().equals(vitVal)
                        || vit.getType().equals(vitVar)
                        || vit.getType().equals(vitCall)
                        || vit.getType().equals(vitInvoke)
        ) {
            return vit._UNPACK_();
        }
        if (vit.getType().equals(VitErrorFw.vitError))
            //noinspection DataFlowIssue
            throw new ExprVitCompilationException(ExprFw.unwrap((Val) vit.get("expr")), ((Val) vit.get("message"))._UNPACK_());

        if (expr == null)
            throw new ExprVitCompilationException(vit);
        throw new ExprVitCompilationException(expr);
    }

    public static final CompEnv directivesCenv = CompEnv.of(FW.telephonist_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = (Val) arg.call((Value) FW.symbol("expr"));
            Val compEnv = (Val) arg.call((Value) FW.symbol("comp-env"));
            Expr expr = (Expr) ExprFw.unwrap(exprVal);
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
                        Val val1 = ((Val) exprVal.call((Value) DIntFw.dint(1)));
                        Expr eee = (Expr) ExprFw.unwrap(val1);
                        Val retVit = (Val) compEnv.call((Value) CompEnv.syntaxResolve(eee, CompEnv.of(compEnv)));
                        if (!VitFw.isVit(retVit.getType()))
                            return retVit; // compile error idk

                        for (int i = 1; i < (isize - 1); i++) {
                            Val val = ((Val) exprVal.call((Value) DIntFw.dint(i + 1)));
                            Expr eeeN = (Expr) ExprFw.unwrap(val);
                            Val argNVit = (Val) compEnv.call((Value) CompEnv.syntaxResolve(eeeN, CompEnv.of(compEnv)));
                            if (!VitFw.isVit(argNVit.getType()))
                                return argNVit; // compile error idk

                            try {
                                retVit = VitFw.wrap(VitFw.unwrap(retVit, eee).call(VitFw.unwrap(argNVit, eeeN)));
                            } catch (ExprVitCompilationException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        return retVit;
                    }
                    case "invoke!": {
                        if (isize != 2) {
                            return null;
                        }

                        Val val = ((Val) exprVal.call((Value) DIntFw.dint(1)));
                        Val retVit = (Val) compEnv.call((Value) CompEnv.syntaxResolve((Expr) ExprFw.unwrap(val), CompEnv.of(compEnv)));
                        if (!VitFw.isVit(retVit.getType()))
                            return retVit; // compile error idk

                        Vit vit = VitUtils.simplify(retVit._UNPACK_());

                        return VitFw.wrap(Vit.invoke(vit));
                    }
                    case "compile-vit": {
                        if (isize != 2)
                            return null;

                        Val val = ((Val) exprVal.call((Value) DIntFw.dint(1)));
                        return VitFw.wrap(VitUtils.simplify(Vit.val(
                                (Val) compEnv.call((Value) CompEnv.syntaxResolve((Expr) ExprFw.unwrap(val), CompEnv.of(compEnv)))
                        )));
                    }
                    case "compile-vit-fast": {
                        if (isize != 2)
                            return null;

                        Val val = ((Val) exprVal.call((Value) DIntFw.dint(1)));
                        return VitFw.wrap(Vit.val(
                                (Val) compEnv.call((Value) CompEnv.syntaxResolve((Expr) ExprFw.unwrap(val), CompEnv.of(compEnv)))
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
                    vit2exprCenv.asValue(),
                    VitFw.directivesCenv.asValue()
            )
    );
}
