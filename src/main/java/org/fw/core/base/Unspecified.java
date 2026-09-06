package org.fw.core.base;

import org.fw.core.FW;
import org.fw.core.util.FwUtils;

import java.util.Objects;

public final class Unspecified {
    public static final Val isUnspecified = FwUtils.valify(Unspecified::isUnspecified);
    private static final Type unspecified_t = FW.telephonist_native((arg) -> {
        if (FwUtils.isTypeApiCall(arg, Unspecified.unspecified_t)) {
            Val instance = (Val) CallFw.getVal(arg);
            arg = (Val) CallFw.getArg(arg);
            return unspecified(instance, arg); // accumulate
        } else if (arg.getType() == SymbolFw.symbol) {
            String v = arg._UNPACK().toString();
            switch (v) {
                case "builder":
                    return FW.telephonist_native("Unspecified.builder",
                            (func) -> FW.telephonist_native((argument) -> unspecified(func, argument)));
                case "val":
                    return FW.telephonist_native(unspecified -> {
                        if (isUnspecified(unspecified))
                            return unspecified._UNPACK(UnspecifiedRecord.class).val();
                        return null;
                    });
                case "arg":
                    return FW.telephonist_native(unspecified -> {
                        if (isUnspecified(unspecified))
                            return unspecified._UNPACK(UnspecifiedRecord.class).arg();
                        return null;
                    });
            }
        }
        return null;
    }).asType();

    public static Val unspecified(Val val, Val arg) {
        return Val.of(unspecified_t, new UnspecifiedRecord(val, arg));
    }

    public static boolean isUnspecified(Val val) {
        return val.getType() == unspecified_t;
    }

    public static Val getVal(Val val) {
        return val._UNPACK(UnspecifiedRecord.class).val;
    }

    public static Val getArg(Val val) {
        return val._UNPACK(UnspecifiedRecord.class).arg;
    }

    private static final class UnspecifiedRecord {
        private final Val val;
        private final Val arg;

        private UnspecifiedRecord(Val val, Val arg) {
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
            UnspecifiedRecord that = (UnspecifiedRecord) obj;
            return Objects.equals(this.val, that.val) &&
                    Objects.equals(this.arg, that.arg);
        }

        @Override
        public int hashCode() {
            return Objects.hash(val, arg);
        }

        @Override
        public String toString() {
            return "UnspecifiedRecord[" + val + ", " + arg + ']';
        }
    }
}
