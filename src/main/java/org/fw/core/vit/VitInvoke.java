package org.fw.core.vit;

import org.fw.core.base.Context;
import org.fw.core.base.Val;
import org.fw.core.lib.VitFw;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;
import org.fw.core.lib.state.OperationFw;

import java.util.Objects;

public final class VitInvoke extends Vit {

    private final Vit operation;

    public VitInvoke(Vit operation) {
        this.operation = Objects.requireNonNull(operation);
//        this.operation = Vit.simplify(operation, null);
    }

    public Vit operation() {
        return operation;
    }

    @Override
    public Val eval(Context context) {
        Operation op = OperationFw.unwrap(operationVal(context));
        if (op == null) {
            throw new IllegalStateException(operation.eval(context).toExpr(context).toString() + " from " + VitFw.wrap(operation).toExpr(context));
        }
//        if (!Operation.isLocal(op, context.scope(), context))
//            return Val.unspecified;
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

    public Val operationVal(Context context) {
        return operation.eval(context);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VitInvoke)) return false;
        VitInvoke that = (VitInvoke) o;
        return Objects.equals(operation, that.operation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operation);
    }

    @Override
    public String toString() {
        return "VitInvoke[operation=" + operation + "]";
    }
}