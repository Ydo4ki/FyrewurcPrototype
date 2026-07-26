package org.fw.core.lib.state;

import org.fw.core.FW;
import org.fw.core.util.FwUtils;
import org.fw.core.annotation.Insightful;
import org.fw.core.base.Call;
import org.fw.core.base.Context;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.DIntFw;
import org.fw.core.lib.VitFw;
import org.fw.core.lib.comp.InternalSystemContext;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.ExprCallOpFw;
import org.fw.core.state.obj.Obj;
import org.fw.core.state.operation.*;
import org.fw.core.vit.RtEnv;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;

import java.math.BigInteger;

import static org.fw.core.FW.symbol;

public final class OperationFw {

    public static final Type operation = FW.telephonist("Operation", (arg, context) -> {
        return Val.unspecified;
    }).asType();

    @Deprecated
    private static Val handleRAW_old(Type type, Val arg, Context context) {
        if (FwUtils.isTypeApiCall(arg, type, context)) {
            Val instance = Call.getVal(arg, context);
            arg = Call.getArg(arg, context);
            return readAndWrite_old(instance._unpack(Operation.class), arg, context);
        }
        return Val.unspecified;
    }

    @Deprecated // I know this is wrong I just don't know how to fix it and whether I really need it at all
    private static Val readAndWrite_old(Operation operation, Val arg, Context context) {
//        if (arg.equals(symbol("reads")))
//            return DVecFw.vec(operation.reads(context).stream().map(StateHoleFw::wrap).toArray(Val[]::new));
//        if (arg.equals(symbol("writes")))
//            return DVecFw.vec(operation.writes(context).stream().map(StateHoleFw::wrap).toArray(Val[]::new));
        return Val.unspecified;
    }

    @Insightful
    @Deprecated
    public static final Type readOperation_old = FW.telephonist("ReadOperation", (arg1, context) -> {
        if (arg1.equals(symbol("constructor"))) {
            return FW.telephonist(() -> "ReadOperation.constructor", (arg, c) -> {
                if (!arg.type().equals(StateHoleFw.statehole)) {
                    return Val.unspecified;
                }
                Obj object = arg._unpack();
                return wrap(Operation.read((Obj.ValObj) object));
            });
        } else if (arg1.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg1.call(symbol("size"), context);
            Val cEnv = arg1.call(symbol("comp-env"), context);
            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 1) return Val.unspecified;

            Val retVit = cEnv.call(CompEnv.syntaxResolve(arg1.call(DIntFw.dint(0), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(retVit.type()))
                return retVit; // compile error idk

            try {
                return VitFw.wrap(Vit.val(OperationFw.readOperation_old.asVal()).call(symbol("constructor")).call(VitFw.unwrap(retVit)));
            } catch (VitCompilationException e) {
                throw new RuntimeException(e);
            }
        }
//        else if (arg1.type().equals(ExprFw.toExpr)) {
//            Val instance = BoxFw.unbox(arg1);
//            if (!instance.type().equals(OperationFw.readOperation))
//                return Val.unspecified; // wrong type
//
//            return ExprFw.wrap(instance._unpack(Operation.class).toExpr(context));
//        }
        return handleRAW_old(OperationFw.readOperation_old, arg1, context);
    }).asType();

    @Insightful
    @Deprecated
    public static final Type localScopeOperation_old = FW.telephonist("LocalScopeOperation", (arg1, context) -> {
        if (arg1.equals(symbol("instance"))) {
            return GetLocalStateOperation.getInstance().asVal();
        } else if (arg1.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg1.call(symbol("size"), context);
            Val cEnv = arg1.call(symbol("comp-env"), context);
            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 0) return Val.unspecified;

            return VitFw.wrap(Vit.val(OperationFw.localScopeOperation_old.asVal()).call(symbol("instance")));
        }
//        else if (arg1.type().equals(ExprFw.toExpr)) {
//            Val instance = BoxFw.unbox(arg1);
//            if (!instance.type().equals(OperationFw.readOperation))
//                return Val.unspecified; // wrong type
//
//            return ExprFw.wrap(instance._unpack(Operation.class).toExpr(context));
//        }
        return handleRAW_old(OperationFw.localScopeOperation_old, arg1, context);
    }).asType();


    @Insightful
    @Deprecated
    public static final Type writeOperation_old = FW.telephonist("WriteOperation", (arg1, context) -> {
        if (arg1.equals(symbol("builder"))) {
            return FW.telephonist(() -> "(get WriteOperation builder)", (arg, context1) -> {
                if (!arg.type().equals(StateHoleFw.statehole)) {
                    return Val.unspecified;
                }
                Obj object = arg._unpack();
                return FW.telephonist(() -> "(call (get WriteOperation builder) " + arg.toExpr(context1) + ")", (arg2, context2) -> {
                    if (!OperationFw.isOperation_old(arg2.type()))
                        return Val.unspecified;
                    return wrap(Operation.write((Obj.ValObj) object, OperationFw.unwrap_old(arg2)));
                });
            });
        } else if (arg1.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg1.call(symbol("size"), context);
            Val cEnv = arg1.call(symbol("comp-env"), context);
            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 2) return Val.unspecified;

            Val retVit = cEnv.call(CompEnv.syntaxResolve(arg1.call(DIntFw.dint(0), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(retVit.type()))
                return retVit; // compile error idk

            Val writeValue = cEnv.call(CompEnv.syntaxResolve(arg1.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(writeValue.type()))
                return writeValue; // compile error idk

            try {
                return VitFw.wrap(Vit.val(OperationFw.writeOperation_old.asVal()).call(symbol("builder")).call(VitFw.unwrap(retVit)).call(VitFw.unwrap(writeValue)));
            } catch (VitCompilationException e) {
                throw new RuntimeException(e);
            }
        }
//        else if (arg1.type().equals(ExprFw.toExpr)) {
//            Val instance = BoxFw.unbox(arg1);
//            if (!instance.type().equals(OperationFw.writeOperation))
//                return Val.unspecified; // wrong type
//
//            return ExprFw.wrap(instance._unpack(Operation.class).toExpr(context));
//        }
        return handleRAW_old(OperationFw.writeOperation_old, arg1, context);
    }).asType();

    @Insightful
    @Deprecated
    public static final Type vitOperation_old = FW.telephonist("VitOperation", (arg1, context) -> {
        if (arg1.equals(symbol("builder"))) {
            return FW.telephonist(() -> "(get VitOperation builder)", (arg, ctx) -> {
                if (!VitFw.isVit(arg.type())) {
                    return Val.unspecified;
                }
                Vit vit = arg._unpack();
//                vit = Vit.reduce(vit, ctx);
                return FW.telephonist(() -> "(call (get VitOperation builder) " + arg.toExpr(context) + ")", (rtEnv, context1) -> {
                    return wrap(Operation.vit(Vit.reduce(vit, new Context(RtEnv.of(rtEnv), context1.scope()))));
                });
            });
        } else if (arg1.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg1.call(symbol("size"), context);
            Val cEnv = arg1.call(symbol("comp-env"), context);
            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 2) return Val.unspecified;

            Val retVit = cEnv.call(CompEnv.syntaxResolve(arg1.call(DIntFw.dint(0), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(retVit.type()))
                return retVit; // compile error idk

            Val varVit = cEnv.call(CompEnv.syntaxResolve(arg1.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(varVit.type()))
                return varVit; // compile error idk

            try {
                return VitFw.wrap(Vit.val(OperationFw.vitOperation_old.asVal()).call(symbol("builder")).call(VitFw.unwrap(retVit)).call(VitFw.unwrap(varVit)));
            } catch (VitCompilationException e) {
                throw new RuntimeException(e);
            }
        }
//        else if (arg1.type().equals(ExprFw.toExpr)) {
//            Val instance = BoxFw.unbox(arg1);
//            if (!instance.type().equals(OperationFw.vitOperation))
//                return Val.unspecified; // wrong type
//
//            return ExprFw.wrap(instance._unpack(Operation.class).toExpr(context));
//        }
        return handleRAW_old(OperationFw.vitOperation_old, arg1, context);
    }).asType();

    @Insightful
    public static final Val operationExpr = FW.telephonist("operation", (arg, context1) -> {
        if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"), InternalSystemContext.context);
            Val cEnv = arg.call(symbol("comp-env"), InternalSystemContext.context);

            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 1)
                return Val.unspecified;

            Val expr = arg.call(DIntFw.dint(0), InternalSystemContext.context);
            Vit vit = null;
            try {
                vit = CompEnv.of(cEnv).compile(expr, context1);
            } catch (VitCompilationException e) {
                return Val.unspecified;
            }
//                return VitFw.wrap(Vit.val(OperationFw.wrap(Operation.vit(vit, context1))));
            vit = Vit.simplify(vit, context1);
            return VitFw.wrap(Vit.val(OperationFw.vitOperation_old.asVal()).call(symbol("builder")).call(VitFw.wrap(vit)).call(Vit.var));
        }
        return Val.unspecified;
    });

    public static Val wrap(Operation operation) {
        if (operation == null) return Val.unspecified;
        return operation.asVal();
//        return switch (operation) {
//            case ReadOperation _ -> Val.of(readOperation, operation);
//            case VitOperation _ -> Val.of(vitOperation, operation);
//            case WriteOperation _ -> Val.of(writeOperation, operation);
//            case LocalScopeOperation _ -> localScopeOperationInstance;
//            default -> throw new IllegalStateException("Unexpected value: " + operation);
//        };
    }

    @Deprecated
    public static Operation unwrap_old(Val operation) {
        if (isOperation_old(operation.type()))
            return operation._unpack(Operation.class);
        return null;
    }

    public static Operation unwrap(Val operation) {
        if (operation.type() == OperationFw.operation)
            return operation._unpack(Operation.class);
        return null;
    }

    @Deprecated
    public static boolean isOperation_old(Type type) {
        return type.equals(readOperation_old)
                || type.equals(writeOperation_old)
                || type.equals(vitOperation_old)
                || type.equals(localScopeOperation_old);
    }
}
