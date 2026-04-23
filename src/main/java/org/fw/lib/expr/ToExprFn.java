package org.fw.lib.expr;

import org.fw.FW;
import org.fw.ast.BracketsTypes;
import org.fw.ast.Expr;
import org.fw.ast.ExprList;
import org.fw.base.TelephonistType;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.lib.VitFw;
import org.fw.vit.VitCall;
import org.fw.vit.VitInvoke;
import org.fw.vit.VitVal;

import java.util.ArrayList;
import java.util.List;

public class ToExprFn {
    public static final Val toExpr = FW.telephonist((arg, context) -> {
        Type type = arg.type();
        if (type.equals(Val.ofTelephonist(0).asType())) {
            TelephonistType.Telephonist tele = arg._unpack();
            var r = tele.representation();
            if (r != null)
                return ExprFw.wrap(r.get());
        } else if (type.equals(VitFw.vitVal)) {
            VitVal vitVal = arg._unpack();
            return ExprFw.wrap(ExprList.of(BracketsTypes.round, type.asVal().toExpr(context), vitVal.val().toExpr(context)));
        } else if (type.equals(VitFw.vitVar)) {
            return ExprFw.wrap(ExprList.of(BracketsTypes.round, type.asVal().toExpr(context)));
        } else if (type.equals(VitFw.vitCall)) {
            VitCall vitVal = arg._unpack();
            List<Expr> elements = new ArrayList<>();
            elements.add(type.asVal().toExpr(context));
            elements.addAll(vitVal.exprs(context));

            return ExprFw.wrap(ExprList.of(BracketsTypes.round, elements));
        } else if (type.equals(VitFw.vitInvoke)) {
            VitInvoke vitInvoke = arg._unpack();
            return ExprFw.wrap(ExprList.of(BracketsTypes.round, type.asVal().toExpr(context), VitFw.wrap(vitInvoke.operation()).toExpr(context)));
        }
        if (type instanceof TelephonistType) {
            return ExprFw.wrap(ExprList.of(BracketsTypes.braces));
        }
        return ExprFw.wrap(ExprList.of(BracketsTypes.braces, arg.type().asVal().toExpr(context)));
    });
}
