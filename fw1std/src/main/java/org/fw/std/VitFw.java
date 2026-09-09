package org.fw.std;

import com.ydo4ki.fw.internal.lib.ConstraintFw;
import org.fw.base.*;
import org.fw.core.FW;

import org.fw.core.util.FwUtils;
import org.fw.core.vit.*;

import static org.fw.core.FW.symbol;

public final class VitFw {

    public static final Type vitVal = FW.lambda_native("VitVal", (arg0) -> {
        if (FwUtils.isTypeApiCall(arg0, VitFw.vitVal)) {
            Val instance2 = (Val) CallFw.getVal(arg0);
            Val callArg = (Val) CallFw.getArg(arg0);
            if (!callArg.getType().equals(SymbolFw.symbol)) {
                return null;
            }
            String symbol1 = callArg._UNPACK_().toString();
            switch (symbol1) {
                case "val":
                    return ((VitVal) instance2._UNPACK_()).val();
                default:
                    return null;
            }
        }
        if (arg0.equalsSymbol("construct")) {
            return FW.lambda_native("VitVal.construct", (arg) -> wrap(Vit.val(arg)));
        }
        return null;
    }).asType();

    public static final Type vitInvoke = FW.lambda_native("VitInvoke", (arg0) -> {
        if (FwUtils.isTypeApiCall(arg0, VitFw.vitInvoke)) {
            Val instance2 = (Val) CallFw.getVal(arg0);
            Val callArg = (Val) CallFw.getArg(arg0);
            if (!callArg.getType().equals(SymbolFw.symbol)) {
                return null;
            }
            String symbol1 = callArg._UNPACK_();
            switch (symbol1) {
                case "operation":
                    return VitFw.wrap(((VitInvoke) instance2._UNPACK_()).operation());
                default:
                    return null;
            }
        }
        if (arg0.equalsSymbol("construct")) {
            return FW.lambda_native("VitInvoke.construct", (arg) -> {
                    if (!VitFw.isVit(arg.getType()))
                        return null;

                    Vit operation = arg._UNPACK_();
                    operation = VitUtils.simplify(operation);
                    return wrap(Vit.invoke(operation));
                });
        }
        return null;
    }).asType();

    public static final Type vitVar = FW.lambda_native("VitVar", (arg0) -> {
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

    public static final Type vitCall = FW.lambda_native("VitCall", (arg0)
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
            return FW.lambda_native("VitCall.builder", (func) -> {
                if (!isVit(func.getType())) {
                    return null;
                }

                return FW.lambda_native((arg) -> {
                    if (!isVit(arg.getType())) {
                        return null;
                    }
                    return wrap(Vit.call(unwrap0(func), unwrap0(arg)));
                });
            });
        }
        return null;
    }).asType();

    public static final Val evalVit = FW.lambda_native("eval-vit", (arg) -> {
        if (isVit(arg.getType())) {
            Vit vit = arg._UNPACK_();
            return vit.asLambdaVal();
        }
        return null;
    });

    public static final Val simplify = FW.lambda_native("vit-simplify", (arg) -> {
        if (VitFw.isVit(arg.getType())) {
            return VitFw.wrap(VitUtils.simplify(arg._UNPACK_()));
        }
        return null;
    });

    public static final Val reduce = FW.lambda_native("vit-reduce", (arg) -> {
        if (VitFw.isVit(arg.getType())) {
            return FW.lambda_native(env
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

    private static Vit unwrap0(Val vit) {
        if (
                vit.getType().equals(VitFw.vitVal)
                        || vit.getType().equals(VitFw.vitVar)
                        || vit.getType().equals(VitFw.vitCall)
                        || vit.getType().equals(VitFw.vitInvoke)
        ) {
            return vit._UNPACK_();
        }

        throw new IllegalStateException("Not a Vit: " + vit);
    }

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

}
