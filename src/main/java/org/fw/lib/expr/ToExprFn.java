package org.fw.lib.expr;

import org.fw.FW;
import org.fw.ast.BracketsTypes;
import org.fw.ast.Expr;
import org.fw.ast.ExprList;
import org.fw.base.TelephonistType;
import org.fw.base.Val;

public class ToExprFn {
    public static final Val toExpr = FW.telephonist((arg, context) -> {
        if (arg.type().equals(Val.ofTelephonist(0).asType())) {
            TelephonistType.Telephonist tele = arg._unpack();
            var r = tele.representation();
            if (r != null)
                return ExprFw.wrap(r.get());
        }
        return ExprFw.wrap(ExprList.of(BracketsTypes.braces, arg.type().asVal().toExpr(context)));
    });
}
