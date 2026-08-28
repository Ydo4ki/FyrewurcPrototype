package org.fw.core.vit;

import org.fw.core.base.Val;
import org.fw.core.base.context.RtEnv;
import org.fw.core.base.contract.CallContract;
import org.fw.lib.stdlib.StdLib;
import org.fw.lib.stdlib.VitFw;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;
import org.fw.lib.stdlib.state.OperationFw;
import org.fw.lib.stdlib.expr.CompEnv;

import java.util.Objects;

public final class VitInvoke extends Vit {

    private Vit operation;

    public VitInvoke(Vit operation) {
        this.operation = Objects.requireNonNull(operation);
    }

    public Vit operation() {
        return operation;
    }

    @Override
    public Val eval(RtEnv rtEnv, State state) {
        Operation op = OperationFw.unwrap(operationVal(rtEnv, state));
        if (op == null) {
            // temp
            CompEnv toExpr = CompEnv.of(StdLib.lib.exports());
            throw new IllegalStateException(operation.eval(rtEnv, state).toExpr(toExpr).toString() + " from " + VitFw.wrap(operation).toExpr(toExpr));
        }
//        if (!Operation.isLocal(op, context.scope(), context))
//            return Val.unspecified;
        return op.apply(state);
    }

    @Override
    public boolean isConst() {
        return operation.isConst();
    }

    @Override
    public boolean isPure() {
        // uhhh
        // I dunno operation are you pure?
//        return operation.isPure();
        // WAIT
        operation = VitUtils.simplify(operation);
        if (operation instanceof VitVal) {
            Val val = ((VitVal) operation).val();
            if (val.type() == OperationFw.operation) {
                Operation op = val._unpack();
                return op.operationAreYouPureQuestionMark();
            }
        }
        return false;
    }

    @Override
    public CallContract evalContract() {
        return CallContract.unknown();
    }

    public Val operationVal(RtEnv rtEnv, State state) {
        return operation.eval(rtEnv, state);
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