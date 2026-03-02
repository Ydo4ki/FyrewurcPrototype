package org.fw.base;

import org.fw.FW;
import org.fw.lib.DIntFw;
import org.fw.lib.VitFw;
import org.fw.lib.expr.CompEnv;
import org.fw.lib.expr.ExprCallOpFw;
import org.fw.vit.Vit;

import java.math.BigInteger;

import static org.fw.FW.symbol;
import static org.fw.FW.telephonist;

// Remember local runtimes
public final class Call {
    public static final Type call_t = telephonist("Call", (arg, context) -> {
        if (arg.type().equals(Call.call_t)) {
            // native
            Call.CallRecord call = arg._unpack();
            Val me = call.val();
            Val cArg = call.arg();
            Call.CallRecord meCall = me._unpack();
            if (cArg.equals(symbol("arg"))) return meCall.arg();
            if (cArg.equals(symbol("val"))) return meCall.val();
        } else if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"), context);
            Val cEnv = arg.call(symbol("comp-env"), context);
            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 2) {
                return Val.unspecified;
            }
            Vit retVit = Vit.val(Call.call_t.asVal()).call(symbol("builder"));

            Val arg0Vit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(0), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(arg0Vit.type()))
                return arg0Vit; // compile error idk

            retVit = retVit.call(VitFw.unwrap(arg0Vit));

            Val arg1Vit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(arg1Vit.type()))
                return arg1Vit; // compile error idk

            retVit = retVit.call(VitFw.unwrap(arg1Vit));

            return VitFw.wrap(retVit);
        } else if (arg.equals(symbol("builder"))) {
            return FW.telephonist("Call.builder", (func, context1) -> {
                return FW.telephonist(() -> "(Call.builder " + func.toExpr(context1) + ")", (argument, context2) -> {
                    return fwCall(func, argument);
                });
            });
        }
        return Val.unspecified;
    }).asType();

    public static Val fwCall(Val instance, Val arg) {
        return Val.of(call_t, new CallRecord(instance, arg));
    }

    public static Val getVal(Val call, Context ctx) {
        return call.call(symbol("val"), ctx);
    }

    public static Val getArg(Val call, Context ctx) {
        return call.call(symbol("arg"), ctx);
    }

    private record CallRecord(Val val, Val arg) { }
}
