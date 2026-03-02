package org.fw.state.operation;

import org.fw.FW;
import org.fw.ast.Expr;
import org.fw.base.Context;
import org.fw.base.Val;
import org.fw.lib.state.OperationFw;
import org.fw.state.obj.Scope;
import org.fw.state.obj.Obj;
import org.fw.vit.Vit;
import org.fw.vit.VitInvoke;
import org.fw.vit.VitVal;

import java.util.Set;

public sealed interface Operation permits LocalScopeOperation, ReadOperation, VitOperation, WriteOperation {
    Val unit = FW.telephonist("unit", (arg, context) -> Operation.unit);

    Val execute(Context context);

    Set<Obj> reads(Context context);

    Set<Obj> writes(Context context);

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

    Expr toExpr(Context context);
}

