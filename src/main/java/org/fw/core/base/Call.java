package org.fw.core.base;

import org.fw.core.FW;

import java.util.Objects;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

// Remember local runtimes
// what
// what is local runtimes
// what do i need to remember
// aaioasopdiou when was this even written
public final class Call {
    public static final Type call_t = telephonist("Call", (arg) -> {
        if (arg.type().equals(Call.call_t)) {
            // native
            Call.CallRecord call = arg._unpack();
            Val me = call.val();
            Val cArg = call.arg();
            Call.CallRecord meCall = me._unpack();
            if (cArg.equals(symbol("arg"))) return meCall.arg();
            if (cArg.equals(symbol("val"))) return meCall.val();
        } else if (arg.equals(symbol("builder"))) {
            return FW.telephonist("Call.builder", (func) -> FW.telephonist((argument) -> fwCall(func, argument)));
        }
        return null;
    }).asType();

    public static Val fwCall(Val instance, Val arg) {
        return Val.of(call_t, new CallRecord(instance, arg));
    }

    public static Val getVal(Val call) {
        return call.call(symbol("val"));
    }

    public static Val getArg(Val call) {
        return call.call(symbol("arg"));
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
