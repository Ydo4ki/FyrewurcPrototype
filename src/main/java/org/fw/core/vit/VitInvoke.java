package org.fw.core.vit;

import org.fw.core.base.Val;
import org.fw.core.base.context.RtEnv;
import org.fw.core.lib.VitFw;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;
import org.fw.core.state.operation.OperationFw;

import java.util.Objects;

import static org.fw.core.FW.symbol;

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
            throw new IllegalStateException(operation.eval(rtEnv, state).toExpr(rtEnv.get(symbol("to-expr"))).toString() + " from " + VitFw.wrap(operation).toExpr(rtEnv.get(symbol("to-expr"))));
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
        operation = Vit.simplify(operation);
        if (operation instanceof VitVal) {
            Val val = ((VitVal) operation).val();
            if (val.type() == OperationFw.operation) {
                Operation op = val._unpack();
                return op.operationAreYouPureQuestionMark();
            }
        }
        return false;
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