package org.fw.core.state.operation;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.base.Context;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.state.OperationFw;
import org.fw.core.state.obj.Scope;
import org.fw.core.state.obj.Obj;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitInvoke;
import org.fw.core.vit.VitVal;

import java.util.Set;

public interface Operation {
    Val unit = FW.telephonist("unit", (arg, context) -> Operation.unit);

    Val execute(Context context);

    @Deprecated
    default Set<Obj> reads(Context context) {
        return Set.of();
    }

    @Deprecated
    default Set<Obj> writes(Context context) {
        return Set.of();
    }

    static Operation read(Obj obj) {
        return new ReadOperation(obj);
    }

    static Operation write(Obj obj, Operation x) {
        return new WriteOperation(obj, x);
    }

    static Operation vit(Vit vit) {
        if (vit instanceof VitInvoke(Vit operation) && operation instanceof VitVal(Val val)) return OperationFw.unwrap(val);
        return new VitOperation(vit);
    }

    static Operation vit(Vit vit, Context context) {
        return vit(Vit.simplify(vit, context));
    }

    static Operation pure(Val val) {
        return new VitOperation(Vit.val(val));
    }

    static boolean isLocal(Operation operation, Scope scope, Context context) {
        for (Obj obj : operation.reads(context)) {
            if (obj.owner() != scope) return false;
        }
        for (Obj obj : operation.writes(context)) {
            if (obj.owner() != scope) return false;
        }
        return true;
    }

    default Expr toExpr(Context context) {
        return ExprList.of(BracketsTypes.braces);
    }

    Val asVal();
}

