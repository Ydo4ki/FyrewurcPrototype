package org.fw.core.vit;

import org.fw.base.MagicPowers;
import org.fw.core.abstrait.Value;
import org.fw.base.Val;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;

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
    public Value eval(Value rtEnv, State state) {
        Value opv = operationVal(rtEnv, state);
//        if (op == null) {
//            // temp
//            CompEnv toExpr = CompEnv.of(StdLib.lib.exports());
//            throw new IllegalArgumentException(operation.eval(rtEnv, state).toExpr(toExpr).toString() + " from " + VitFw.wrap(operation).toExpr(toExpr));
//        }
        return opv.invoke(state);
    }

    @Override
    public boolean isConst() {
        return operation.isConst();
    }

    private static final MagicPowers magic = MagicPowers.getMagicPowers();

    @Override
    public boolean isPure() {
        // uhhh
        // I dunno operation are you pure?
//        return operation.isPure();
        // WAIT
        operation = VitUtils.simplify(operation);
        if (operation instanceof VitVal) {
            Val val = ((VitVal) operation).val().asVal(null);
            Object u = magic.unpackVal(val);
            if (val != null && u instanceof Operation) {
                Operation op = (Operation)u;
                return op.operationAreYouPureQuestionMark();
            }
        }
        return false;
    }

    public Value operationVal(Value rtEnv, State state) {
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