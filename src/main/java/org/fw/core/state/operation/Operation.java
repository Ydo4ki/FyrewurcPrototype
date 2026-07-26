package org.fw.core.state.operation;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.base.Context;
import org.fw.core.base.Val;
import org.fw.core.lib.state.LaserPointerFw;
import org.fw.core.lib.state.OperationFw;
import org.fw.core.state.obj.State;
import org.fw.core.state.obj.Obj;
import org.fw.core.vit.RtEnv;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitInvoke;
import org.fw.core.vit.VitVal;

public abstract class Operation {
    // a Val symbolizing successful completion of the operation
    public static final Val unit = FW.telephonist("unit", (arg, context) -> Operation.unit);
    public static final State systemState = State.eternal();

    public abstract Val execute(Context context);

    private final Val asVal;

    protected Operation() {
        this.asVal = Val.of(OperationFw.operation, this);
    }

    public static Operation read(Obj.ValObj obj) {
        return new ReadOperation(obj);
    }

    public static Operation write(Obj.ValObj obj, Val x) {
        return new WriteOperation(obj, x);
    }

    public static Operation vit(Vit vit, RtEnv rtEnv) {
        if (vit instanceof VitInvoke && ((VitInvoke) vit).operation() instanceof VitVal)
            return OperationFw.unwrap_old(((VitVal) ((VitInvoke) vit).operation()).val());
        return new VitOperation(vit, rtEnv);
    }

    @Deprecated
    public static Operation vit(Vit vit, RtEnv rtEnv, Context context) {
        return vit(Vit.simplify(vit, context), rtEnv);
    }

    public static Operation pure(Val val) {
        return new VitOperation(Vit.val(val), RtEnv.unspecified);
    }

    public Expr toExpr(Context context) {
        return ExprList.of(BracketsTypes.braces);
    }

    public final Val asVal() {
        return asVal;
    }

    public static class HelloWorldOperation extends Operation {

        @Override
        public Val execute(Context context) {
            // errr ok I'm not sure how to determine if that's a system context or not
            // and it's not like it will be much useful later
            // I should probably create a random instance and call it a system context
            if (context.state() != systemState) {
                return Val.unspecified;
            }
            System.out.println("Hello World!!!");
            return Operation.unit;
        }
    }

    public static class CreateNewObjectOperation extends Operation {

        private final Val initialValue;

        public CreateNewObjectOperation(Val initialValue) {
            this.initialValue = initialValue;
        }

        @Override
        public Val execute(Context context) {
            Obj.ValObj obj = new Obj.ValObj(initialValue, context.state());
            return Val.of(LaserPointerFw.laserPointer, obj);
        }
    }
}

