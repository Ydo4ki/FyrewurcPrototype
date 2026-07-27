package org.fw.core.lib.telephonist;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;

final class UnpackerFw {
    public static final Type unpacker = FW.telephonist("Unpacker", (arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, UnpackerFw.unpacker, context)) {
            Val instance = Call.getVal(arg, context);
            Val cArg = Call.getArg(arg, context);

            Unpacker unpkg = instance._unpack();
            if (!cArg.type().equals(unpkg.type()) || !(cArg._unpack() instanceof Val)) {
                return null; // wrong unpacker / unsupported value / consider using boxes
            }
            return cArg._unpack(Val.class);
        }
        return null;
    }).asType();

    private static final class Unpacker {
        private final Type type;
        private final Val source;
        private final String name;

        private Unpacker(Type type, Val source, String name) {
            this.type = type;
            this.source = source;
            this.name = name;
        }

        Type type() {
            return type;
        }

        Expr toExpr(Context context) {
            return ExprList.of(BracketsTypes.round,
                    Symbol.of("get"),
                    source.toExpr(context),
                    Symbol.of(name)
            );
        }
    }

    public static Val mkUnpacker(Type type, Val source, String name) {
        return Val.of(unpacker, new Unpacker(type, source, name));
    }
}
