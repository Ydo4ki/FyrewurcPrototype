package org.fw.core.state.operation;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.base.Context;
import org.fw.core.base.Val;
import org.fw.core.lib.state.OperationFw;
import org.fw.core.state.obj.State;
import org.fw.core.state.obj.Obj;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitInvoke;
import org.fw.core.vit.VitVal;

import java.util.Collections;
import java.util.Set;

public abstract class Operation {
    public static final Val unit = FW.telephonist("unit", (arg, context) -> Operation.unit);

    public abstract Val execute(Context context);

    private final Val asVal;

    protected Operation() {
        this.asVal = Val.of(OperationFw.operation, this);
    }

    public static Operation read(Obj.ValObj obj) {
        return new ReadOperation(obj);
    }

    public static Operation write(Obj.ValObj obj, Operation x) {
        return new WriteOperation(obj, x);
    }

    public static Operation vit(Vit vit) {
        if (vit instanceof VitInvoke && ((VitInvoke) vit).operation() instanceof VitVal)
            return OperationFw.unwrap_old(((VitVal) ((VitInvoke) vit).operation()).val());
        return new VitOperation(vit);
    }

    public static Operation vit(Vit vit, Context context) {
        return vit(Vit.simplify(vit, context));
    }

    public static Operation pure(Val val) {
        return new VitOperation(Vit.val(val));
    }

    public Expr toExpr(Context context) {
        return ExprList.of(BracketsTypes.braces);
    }

    public final Val asVal() {
        return asVal;
    }

    public static class HelloWorldOperation extends Operation {

        public static final State systemState = State.eternal();

        @Override
        public Val execute(Context context) {
            // errr ok I'm not sure how to determine if that's a system context or not
            // and it's not like it will be much useful later
            // I should probably create a random instance and call it a system context
            if (context.scope() != systemState) {
                return Val.unspecified;
            }
            System.out.println("Hello World!!!");
            return Operation.unit;
        }
    }
}

