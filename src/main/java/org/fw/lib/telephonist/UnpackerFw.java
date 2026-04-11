package org.fw.lib.telephonist;

import org.fw.FW;
import org.fw.FwUtils;
import org.fw.ast.BracketsTypes;
import org.fw.ast.Expr;
import org.fw.ast.ExprList;
import org.fw.ast.Symbol;
import org.fw.base.Call;
import org.fw.base.Context;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.lib.BoxFw;
import org.fw.lib.expr.ExprFw;

final class UnpackerFw {
    public static final Type unpacker = FW.telephonist("Unpacker", (arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, UnpackerFw.unpacker, context)) {
            Val instance = Call.getVal(arg, context);
            Val cArg = Call.getArg(arg, context);

            Unpacker unpkg = instance._unpack();
            if (!cArg.type().equals(unpkg.type()) || !(cArg._unpack() instanceof Val)) {
//                System.out.println("WRONG!!!");
//                System.out.println(cArg.type());
//                System.out.println(unpkg.type);
                return Val.unspecified; // wrong unpacker / unsupported value / consider using boxes
            }
            return cArg._unpack(Val.class);
        } else if (arg.type().equals(ExprFw.toExpr)) {
            Val instance = BoxFw.unbox(arg);
            if (!instance.type().equals(UnpackerFw.unpacker))
                return Val.unspecified;

            Unpacker unpkg = instance._unpack();
            return ExprFw.wrap(unpkg.toExpr(context));
        }
        return Val.unspecified;
    }).asType();

    private record Unpacker(Type type, Val source, String name) {
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
