package org.fw.base;

import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import org.fw.core.util.FwUtils;

import java.util.Objects;

@Deprecated // todo: replace with other implementations of value
public final class Unspecified {
    public static final Val isUnspecified = FwUtils.valify(Unspecified::isUnspecified);
    private static final Type unspecified_t = FW.lambda_native((arg) -> {
        if (FwUtils.isTypeApiCall(arg, Unspecified.unspecified_t)) {
            Val instance = (Val) CallFw.getVal(arg);
            arg = (Val) CallFw.getArg(arg);
            return unspecified(instance, arg); // accumulate
        } else if (arg.getType() == SymbolFw.symbol) {
            String v = arg._UNPACK_().toString();
            switch (v) {
                case "builder":
                    return FW.lambda_native("Unspecified.builder",
                            (func) -> FW.lambda_native((argument) -> unspecified(func, argument)));
                case "val":
                    return FW.lambda_native(unspecified -> {
                        if (isUnspecified(unspecified))
                            return unspecified._UNPACK_(UnspecifiedRecord.class).val();
                        return null;
                    });
                case "arg":
                    return FW.lambda_native(unspecified -> {
                        if (isUnspecified(unspecified))
                            return unspecified._UNPACK_(UnspecifiedRecord.class).arg();
                        return null;
                    });
            }
        }
        return null;
    }).asType();

    public static Val unspecified(Value val, Value arg) {
        return Val._NEW_INSTANCE_(unspecified_t, new UnspecifiedRecord(val, arg));
    }

    public static boolean isUnspecified(Value val) {
        return val.getTypeValue().impliesEquality(unspecified_t.asVal());
    }

    public static Value getVal(Val val) {
        return val._UNPACK_(UnspecifiedRecord.class).val;
    }

    public static Value getArg(Val val) {
        return val._UNPACK_(UnspecifiedRecord.class).arg;
    }

    private static final class UnspecifiedRecord {
        private final Value val;
        private final Value arg;

        private UnspecifiedRecord(Value val, Value arg) {
            this.val = val;
            this.arg = arg;
        }

        public Value val() {
            return val;
        }

        public Value arg() {
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
