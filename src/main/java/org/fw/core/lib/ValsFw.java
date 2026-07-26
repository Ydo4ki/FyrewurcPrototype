package org.fw.core.lib;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.base.Context;
import org.fw.core.base.Val;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.vit.Vit;

import static org.fw.core.FW.symbol;

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

    public static CompEnv exports = CompEnv.of(CompEnv.compEnv(Context.outOf,
            ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                    DeclaredFw.declared(symbol("eq"), eq),
                    DeclaredFw.declared(symbol("typeGet"), typeGet)
            ))
    ));
}
