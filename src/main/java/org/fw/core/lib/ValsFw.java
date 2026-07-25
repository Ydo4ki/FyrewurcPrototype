package org.fw.core.lib;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.base.Val;
import org.fw.core.vit.Vit;

public final class ValsFw {
    // basic stuff

    public static final Val typeGet = FW.telephonist(
            () -> "type-get",
            (arg, c) -> arg.type().asVal()
    );

    public static final Val eq = FW.telephonist(
            () -> "eq",
            (arg, context) -> {
                return FW.telephonistE(() -> ExprList.of(BracketsTypes.round,
                        Symbol.of("call"),
                        Symbol.of("eq"),
                        arg.toExpr(context)
                        ), (arg1, c) -> {
                    return BoolFw.wrap(arg.equals(arg1));
                });
            }
    );

    public static Vit equals(Vit a, Vit b) {
        return Vit.val(eq).call(a).call(b);
    }
}
