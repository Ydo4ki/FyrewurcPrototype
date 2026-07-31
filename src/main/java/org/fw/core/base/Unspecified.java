package org.fw.core.base;

import org.fw.core.FW;
import org.fw.core.lib.BoolFw;
import org.fw.core.lib.VitFw;
import org.fw.core.lib.constraint.ConstraintFw;
import org.fw.core.util.FwUtils;
import org.fw.core.vit.Vit;

import java.util.Objects;

import static org.fw.core.FW.symbol;

public final class Unspecified {
    private static final Type unspecified_type = FW.telephonist((arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, Unspecified.unspecified_type, context)) {
            Val instance = Call.getVal(arg, context);
            arg = Call.getArg(arg, context);
            return unspecified(instance, arg); // accumulate
        } else if (arg.equals(symbol("builder"))) {
            return FW.telephonist("Unspecified.builder",
                    (func, context1) -> FW.telephonist(() -> "(Unspecified.builder " + func.toExpr(context1) + ")",
                            (argument, context2) -> unspecified(func, argument)));
        }
        return null;
    }).asType();

    public static final Val isUnspecified = FwUtils.valify(Unspecified::isUnspecified);
    public static final Val isNot = ConstraintFw.constraint(
            Vit.val(BoolFw._false),
            Vit.val(isUnspecified).call(Vit.var)
    );

    public static Val unspecified(Val val, Val arg) {
        return Val.of(unspecified_type, new UnspecifiedRecord(val, arg));
    }

    public static boolean isUnspecified(Val val) {
        return val.type() == unspecified_type;
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
