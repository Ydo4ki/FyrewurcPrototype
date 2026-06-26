package org.fw.core.lib;

import org.fw.core.FW;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.Expr;
import org.fw.core.base.Context;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.ExprCallOpFw;
import org.fw.core.state.operation.Operation;
import org.fw.core.lib.state.OperationFw;
import org.fw.core.state.obj.Scope;
import org.fw.core.vit.*;

import java.math.BigInteger;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

public final class VitFw {

    public static final Type vitVal = telephonist("VitVal", (arg0, context0)
            -> FwUtils.handleSymbols(arg0, VitFw.vitVal, context0, (instance, symbol) -> {
        switch (symbol) {
            case "val":
                return instance._unpack(VitVal.class).val();
            default:
                return Val.unspecified;
        }
    }, (arg1, context1) -> {
        if (arg1.equals(symbol("constructor"))) {
            return FW.telephonist(() -> "(get VitVal constructor)", (arg, c) -> {
                return wrap(Vit.val(arg));
            });
        } else if (arg1.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg1.call(symbol("size"), context1);
            Val cEnv = arg1.call(symbol("comp-env"), context1);
            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 1) return Val.unspecified;

            Val retVit = cEnv.call(CompEnv.syntaxResolve(arg1.call(DIntFw.dint(0), context1)._unpack(), CompEnv.of(cEnv)), context1);
            if (!VitFw.isVit(retVit.type()))
                return retVit; // compile error idk

            return VitFw.wrap(Vit.val(VitFw.vitVal.asVal()).call(symbol("constructor")).call(VitFw.unwrap(retVit)));
        }
        return Val.unspecified;
    })).asType();

    public static final Type vitInvoke = telephonist("VitInvoke", (arg0, context0)
            -> FwUtils.handleSymbols(arg0, VitFw.vitInvoke, context0, (instance, symbol) -> {
        switch (symbol) {
            case "operation":
                return VitFw.wrap(instance._unpack(VitInvoke.class).operation());
            default:
                return Val.unspecified;
        }
    }, (arg1, context1) -> {
        if (arg1.equals(symbol("constructor"))) {
            return FW.telephonist(() -> "(get VitInvoke constructor)", (arg, ctx) -> {
                if (!VitFw.isVit(arg.type()))
                    return Val.unspecified;

                Vit operation = arg._unpack();
                operation = Vit.simplify(operation, ctx);
                return wrap(Vit.invoke(operation));
            });
        } else if (arg1.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg1.call(symbol("size"), context1);
            Val cEnv = arg1.call(symbol("comp-env"), context1);
            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 1) return Val.unspecified;

            Val retVit = cEnv.call(CompEnv.syntaxResolve(arg1.call(DIntFw.dint(0), context1)._unpack(), CompEnv.of(cEnv)), context1);
            if (!VitFw.isVit(retVit.type()))
                return retVit; // compile error idk

            return VitFw.wrap(Vit.val(VitFw.vitInvoke.asVal()).call(symbol("constructor")).call(VitFw.unwrap(retVit)));
        }
        return Val.unspecified;
    })).asType();

    public static final Type vitVar = telephonist("VitVar", (arg0, context0)
            -> FwUtils.handleSymbols(arg0, VitFw.vitVar, context0, (instance, symbol) -> {
        switch (symbol) {
//        case "key":
//            return ((VitVar) instance._unpack()).key();
            default:
                return Val.unspecified;
        }
    }, (arg1, context1) -> {
        if (arg1.equals(symbol("instance"))) {
            return wrap(Vit.var);
        } else if (arg1.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg1.call(symbol("size"), context1);
            Val cEnv = arg1.call(symbol("comp-env"), context1);
            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 0) return Val.unspecified;

            return VitFw.wrap(Vit.val(VitFw.vitVar.asVal()).call(symbol("instance")));
        }
        return Val.unspecified;
    })).asType();

    private static final Expr repr = FwUtils.parse("(get VitCall builder)");
    public static final Type vitCall = telephonist("VitCall", (arg0, context0)
            -> FwUtils.handleSymbols(arg0, VitFw.vitCall, context0, (instance, symbol) -> {
        switch (symbol) {
            case "func":
                return wrap(((VitCall) instance._unpack()).func());
            case "arg":
                return wrap(((VitCall) instance._unpack()).arg());
            default:
                return Val.unspecified;
        }
    }, (arg1, context1) -> {
        if (arg1.equals(symbol("builder"))) {
            return telephonist(repr, (func, context) -> {
                if (!isVit(func.type())) {
                    return Val.unspecified;
                }

                return telephonist(() -> "(call " + repr + " " + func.toExpr(context) + ")", (arg, context2) -> {
                    if (!isVit(arg.type())) {
                        return Val.unspecified;
                    }
                    return wrap(Vit.call(unwrap(func), unwrap(arg)));
                });
            });
        } else if (arg1.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg1.call(symbol("size"), context1);
            Val cEnv = arg1.call(symbol("comp-env"), context1);
            int isize = size._unpack(BigInteger.class).intValue();
            if (isize < 2) return Val.unspecified;

            Val retVit = cEnv.call(CompEnv.syntaxResolve(arg1.call(DIntFw.dint(0), context1)._unpack(), CompEnv.of(cEnv)), context1);
            if (!VitFw.isVit(retVit.type()))
                return retVit; // compile error idk

            Val retVit2 = cEnv.call(CompEnv.syntaxResolve(arg1.call(DIntFw.dint(1), context1)._unpack(), CompEnv.of(cEnv)), context1);
            if (!VitFw.isVit(retVit2.type()))
                return retVit2; // compile error idk

            Vit vit = Vit.val(VitFw.vitCall.asVal()).call(symbol("builder"))
                    .call(VitFw.unwrap(retVit))
                    .call(VitFw.unwrap(retVit2));

            for (int i = 2; i < isize; i++) {
                Val retVitN = cEnv.call(CompEnv.syntaxResolve(arg1.call(DIntFw.dint(i), context1)._unpack(), CompEnv.of(cEnv)), context1);
                if (!VitFw.isVit(retVitN.type()))
                    return retVitN; // compile error idk

                vit = Vit.val(VitFw.vitCall.asVal()).call(symbol("builder"))
                        .call(vit)
                        .call(VitFw.unwrap(retVitN));
            }

            return VitFw.wrap(vit);
        }
        return Val.unspecified;
    })).asType();

    public static final Val eval = telephonist("eval", (arg, context) -> {
        if (isVit(arg.type())) {
            Vit vit = arg._unpack();
            return telephonist((env, context1) -> {
                return Scope.performAndDie(context1.scope(), scope ->
                        OperationFw.wrap(Operation.vit(Vit.reduce(vit, new Context(RtEnv.of(env), scope)))));
//                return ;
            });
        }
        return Val.unspecified;
    });

    public static final Val simplify = FW.telephonist("vit-simplify", (arg, context1) -> {
        if (VitFw.isVit(arg.type())) {
            return VitFw.wrap(Vit.simplify(arg._unpack(), context1));
        }
        return Val.unspecified;
    });

    public static final Val reduce = FW.telephonist("vit-reduce", (arg, context1) -> {
        if (VitFw.isVit(arg.type())) {
            return FW.telephonist(() -> "(call vit-reduce " + arg.toExpr(context1) + ")", (env, context2)
                    -> Scope.performAndDie(context2.scope(),
                    scope -> VitFw.wrap(Vit.reduce(arg._unpack(), new Context(RtEnv.of(env), scope))))); // thx java
        }
        return Val.unspecified;
    });

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

    public static Vit unwrap(Val vit) {
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
}
