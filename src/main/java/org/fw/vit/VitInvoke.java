package org.fw.vit;

import org.fw.base.Context;
import org.fw.base.Val;
import org.fw.lib.VitFw;
import org.fw.state.obj.Scope;
import org.fw.state.operation.Operation;
import org.fw.lib.state.OperationFw;

import java.util.Objects;

public final record VitInvoke(Vit operation) implements Vit {
    public VitInvoke {
        Objects.requireNonNull(operation);
//        operation = Vit.simplify(operation, null);
    }

//     I'm not sure if this operation would even make sense after the changes I'm making
    @Override
    public Val eval(Context context) {
        if (!operation.isLocal(context))
            throw new IllegalArgumentException(VitFw.wrap(operation).toExpr(new Context(RtEnv.unspecified, Scope.eternal())) + " is not local");
        Operation op = OperationFw.unwrap(operationVal(context));
        if (op == null) {
            throw new IllegalStateException(operation.eval(context).toExpr(context).toString() + " from " + VitFw.wrap(operation).toExpr(context));
        }
        if (!Operation.isLocal(op, context.scope(), context))
            return Val.unspecified;
        return op.execute(context);
    }

    @Override
    public boolean isConst() {
        return operation.isConst();
    }

    @Override
    public boolean isPure() {
        return false;
    }

    @Override
    public boolean isLocal(Context context) {
        if (!operation.isLocal(context))
            return false;
        Operation op = OperationFw.unwrap(operationVal(context));
        if (op == null) {
            throw new IllegalStateException(operation.eval(context).toExpr(context).toString() + " from " + VitFw.wrap(operation).toExpr(context));
        }
        return Operation.isLocal(op, context.scope(), context);
    }

    public Val operationVal(Context context) {
        return operation.eval(context);
    }
}
