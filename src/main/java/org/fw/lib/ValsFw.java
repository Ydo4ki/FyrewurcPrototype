package org.fw.lib;

import org.fw.FW;
import org.fw.ast.BracketsTypes;
import org.fw.ast.ExprList;
import org.fw.ast.Symbol;
import org.fw.base.Val;

public final class ValsFw {
    // basic stuff

    public static final Val typeGet = FW.telephonist(
            () -> "type-get",
            (arg, _) -> arg.type().asVal()
    );

    public static final Val eq = FW.telephonist(
            () -> "eq",
            (arg, context) -> {
                return FW.telephonistE(() -> ExprList.of(BracketsTypes.round,
                        Symbol.of("call"),
                        Symbol.of("eq"),
                        arg.toExpr(context)
                        ), (arg1, _) -> {
                    return BoolFw.wrap(arg.equals(arg1));
                });
            }
    );
}
