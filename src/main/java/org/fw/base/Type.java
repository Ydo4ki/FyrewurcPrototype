package org.fw.base;

import org.fw.ast.BracketsTypes;
import org.fw.ast.Expr;
import org.fw.ast.ExprList;
import org.fw.lib.expr.ExprFw;

public sealed interface Type permits TelephonistType, Type.ValType {

    static Type of(Val.Box val) {
        return new ValType(val);
    }

    Val callInstance(Val instance, Val arg, Context context);

    Val asVal();

//    Expr instanceToExpr(Val instance, Context context);

    record ValType(Val.Box asVal) implements Type {
        @Override
        public Val callInstance(Val instance, Val arg, Context context) {
            Val ret = asVal.call(Call.fwCall(instance, arg), context);
            // ok I'm not sure why but that doesn't work
//            if (ret == Val.unspecified)
//                return UnspecifiedCallFw.unspecifiedCall(instance, arg);
            return ret;
        }

//        @Override
//        public Expr instanceToExpr(Val instance, Context context) {
//            Val e = asVal.call(Val.of(ExprFw.toExpr, instance), context);
//            // todo: constraint check
//            if (e.type().equals(ExprFw.symbol) || e.type().equals(ExprFw.exprList)) {
//                return e._unpack();
//            }
//            return ExprList.of(BracketsTypes.braces, asVal.toExpr(context));
//        }

        @Override
        public String toString() {
            return asVal.toString();
        }
    }
}
