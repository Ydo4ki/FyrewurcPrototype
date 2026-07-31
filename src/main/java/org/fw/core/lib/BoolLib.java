package org.fw.core.lib;

import org.fw.core.FW;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.base.context.Context;
import org.fw.core.lib.expr.ToExprFn;

import static org.fw.core.FW.symbol;

public class BoolLib {
    public static final Val boolToExpr = FW.telephonist((arg, context) -> {
        Type type = arg.type();
        if (type.equals(BoolFw.bool)) {
            return symbol(arg._unpack().toString());
        }
        return null;
    });

    public static final Lib lib = Lib.of(
            ModuleFw.module(
                    DeclaredFw.declared(symbol("true"), BoolFw._true),
                    DeclaredFw.declared(symbol("false"), BoolFw._false)
            ),
            var -> {
                Context c = Context.outOf;
                return ChainLinkFw.chain(ToExprFn.exprififier,
                        boolToExpr,
                        var.call(symbol("to-expr"), c),
                        c
                );
            }
    );
}
