package org.fw.lib.state;

import org.fw.FW;
import org.fw.FwUtils;
import org.fw.base.Call;
import org.fw.base.Context;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.lib.BoxFw;
import org.fw.lib.DIntFw;
import org.fw.lib.DVecFw;
import org.fw.lib.VitFw;
import org.fw.lib.comp.InternalSystemContext;
import org.fw.lib.expr.CompEnv;
import org.fw.lib.expr.ExprCallOpFw;
import org.fw.lib.expr.ExprFw;
import org.fw.state.obj.Obj;
import org.fw.state.operation.*;
import org.fw.vit.RtEnv;
import org.fw.vit.Vit;

import java.math.BigInteger;

import static org.fw.FW.symbol;

public final class OperationFw {

    private static Val handleRAW(Type type, Val arg, Context context) {
        if (FwUtils.isTypeApiCall(arg, type, context)) {
            Val instance = Call.getVal(arg, context);
            arg = Call.getArg(arg, context);
            return readAndWrite(instance._unpack(Operation.class), arg, context);
        }
        return Val.unspecified;
    }

    @Deprecated // I know this is wrong I just don't know how to fix it and whether I really need it at all
    private static Val readAndWrite(Operation operation, Val arg, Context context) {
        if (arg.equals(symbol("reads")))
            return DVecFw.vec(operation.reads(context).stream().map(StateHoleFw::wrap).toArray(Val[]::new));
        if (arg.equals(symbol("writes")))
            return DVecFw.vec(operation.writes(context).stream().map(StateHoleFw::wrap).toArray(Val[]::new));
        return Val.unspecified;
    }

    public static final Type readOperation = FW.telephonist("ReadOperation", (arg1, context) -> {
        if (arg1.equals(symbol("constructor"))) {
            return FW.telephonist(() -> "(get ReadOperation constructor)", (arg, _) -> {
                if (!arg.type().equals(StateHoleFw.statehole)) {
                    return Val.unspecified;
                }
                Obj object = arg._unpack();
                return wrap(Operation.read(object));
            });
        } else if (arg1.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg1.call(symbol("size"), context);
            Val cEnv = arg1.call(symbol("comp-env"), context);
            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 1) return Val.unspecified;

            Val retVit = cEnv.call(CompEnv.syntaxResolve(arg1.call(DIntFw.dint(0), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(retVit.type()))
                return retVit; // compile error idk

            return VitFw.wrap(Vit.val(OperationFw.readOperation.asVal()).call(symbol("constructor")).call(VitFw.unwrap(retVit)));
        } else if (arg1.type().equals(ExprFw.toExpr)) {
            Val instance = BoxFw.unbox(arg1);
            if (!instance.type().equals(OperationFw.readOperation))
                return Val.unspecified; // wrong type

            return ExprFw.wrap(instance._unpack(Operation.class).toExpr(context));
        }
        return handleRAW(OperationFw.readOperation, arg1, context);
    }).asType();

    public static final Type localScopeOperation = FW.telephonist("LocalScopeOperation", (arg1, context) -> {
        if (arg1.equals(symbol("instance"))) {
            return OperationFw.localScopeOperationInstance;
        } else if (arg1.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg1.call(symbol("size"), context);
            Val cEnv = arg1.call(symbol("comp-env"), context);
            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 0) return Val.unspecified;

            return VitFw.wrap(Vit.val(OperationFw.localScopeOperation.asVal()).call(symbol("instance")));
        } else if (arg1.type().equals(ExprFw.toExpr)) {
            Val instance = BoxFw.unbox(arg1);
            if (!instance.type().equals(OperationFw.readOperation))
                return Val.unspecified; // wrong type

            return ExprFw.wrap(instance._unpack(Operation.class).toExpr(context));
        }
        return handleRAW(OperationFw.localScopeOperation, arg1, context);
    }).asType();

    private static final Val localScopeOperationInstance = Val.of(localScopeOperation, LocalScopeOperation.getInstance());

    public static final Type writeOperation = FW.telephonist("WriteOperation", (arg1, context) -> {
        if (arg1.equals(symbol("builder"))) {
            return FW.telephonist(() -> "(get WriteOperation builder)", (arg, context1) -> {
                if (!arg.type().equals(StateHoleFw.statehole)) {
                    return Val.unspecified;
                }
                Obj object = arg._unpack();
                return FW.telephonist(() -> "(call (get WriteOperation builder) " + arg.toExpr(context1) + ")", (arg2, context2) -> {
                    if (!OperationFw.isOperation(arg2.type()))
                        return Val.unspecified;
                    return wrap(Operation.write(object, OperationFw.unwrap(arg2)));
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

            return VitFw.wrap(Vit.val(OperationFw.writeOperation.asVal()).call(symbol("builder")).call(VitFw.unwrap(retVit)).call(VitFw.unwrap(writeValue)));
        } else if (arg1.type().equals(ExprFw.toExpr)) {
            Val instance = BoxFw.unbox(arg1);
            if (!instance.type().equals(OperationFw.writeOperation))
                return Val.unspecified; // wrong type

            return ExprFw.wrap(instance._unpack(Operation.class).toExpr(context));
        }
        return handleRAW(OperationFw.writeOperation, arg1, context);
    }).asType();

    public static final Type vitOperation = FW.telephonist("VitOperation", (arg1, context) -> {
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

            return VitFw.wrap(Vit.val(OperationFw.vitOperation.asVal()).call(symbol("builder")).call(VitFw.unwrap(retVit)).call(VitFw.unwrap(varVit)));
        } else if (arg1.type().equals(ExprFw.toExpr)) {
            Val instance = BoxFw.unbox(arg1);
            if (!instance.type().equals(OperationFw.vitOperation))
                return Val.unspecified; // wrong type

            return ExprFw.wrap(instance._unpack(Operation.class).toExpr(context));
        }
        return handleRAW(OperationFw.vitOperation, arg1, context);
    }).asType();

    public static final Val operationExpr = FW.telephonist("operation", (arg, context1) -> {
        if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"), InternalSystemContext.context);
            Val cEnv = arg.call(symbol("comp-env"), InternalSystemContext.context);

            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 1)
                return Val.unspecified;

            Val expr = arg.call(DIntFw.dint(0), InternalSystemContext.context);
            Vit vit = CompEnv.of(cEnv).compile(expr, context1);
            if (vit == null)
                return Val.unspecified;
//                return VitFw.wrap(Vit.val(OperationFw.wrap(Operation.vit(vit, context1))));
            vit = Vit.simplify(vit, context1);
            return VitFw.wrap(Vit.val(OperationFw.vitOperation.asVal()).call(symbol("builder")).call(VitFw.wrap(vit)).call(Vit.var));
        }
        return Val.unspecified;
    });

    public static Val wrap(Operation operation) {
        if (operation == null) return Val.unspecified;
        return switch (operation) {
            case ReadOperation _ -> Val.of(readOperation, operation);
            case VitOperation _ -> Val.of(vitOperation, operation);
            case WriteOperation _ -> Val.of(writeOperation, operation);
            case LocalScopeOperation _ -> localScopeOperationInstance;
        };
    }

    public static Operation unwrap(Val operation) {
        if (isOperation(operation.type()))
            return operation._unpack(Operation.class);
        return null;
    }

    public static boolean isOperation(Type type) {
        return type.equals(readOperation)
                || type.equals(writeOperation)
                || type.equals(vitOperation)
                || type.equals(localScopeOperation);
    }
}
