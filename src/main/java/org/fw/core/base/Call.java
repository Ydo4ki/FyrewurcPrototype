package org.fw.core.base;

import org.fw.core.FW;
import org.fw.core.lib.DIntFw;
import org.fw.core.lib.VitFw;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.ExprCallOpFw;
import org.fw.core.vit.Vit;

import java.math.BigInteger;
import java.util.Objects;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

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

            retVit = retVit.call(VitFw.unwrap0(arg0Vit));

            Val arg1Vit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(arg1Vit.type()))
                return arg1Vit; // compile error idk

            retVit = retVit.call(VitFw.unwrap0(arg1Vit));

            return VitFw.wrap(retVit);
        } else if (arg.equals(symbol("builder"))) {
            return FW.telephonist("Call.builder",
                    (func, context1) -> FW.telephonist(() -> "(Call.builder " + func.toExpr(context1) + ")",
                            (argument, context2) -> fwCall(func, argument)));
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

    private static final class CallRecord {
        private final Val val;
        private final Val arg;

        private CallRecord(Val val, Val arg) {
            this.val = val;
            this.arg = arg;
        }

        public Val val() {
            return val;
        }

        public Val arg() {
            return arg;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            CallRecord that = (CallRecord) obj;
            return Objects.equals(this.val, that.val) &&
                    Objects.equals(this.arg, that.arg);
        }

        @Override
        public int hashCode() {
            return Objects.hash(val, arg);
        }

        @Override
        public String toString() {
            return "CallRecord[" +
                    "val=" + val + ", " +
                    "arg=" + arg + ']';
        }
    }
}
