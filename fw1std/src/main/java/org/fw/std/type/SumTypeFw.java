
package org.fw.std.type;

import org.fw.base.CallFw;
import org.fw.base.DefinitiveValEnv;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import org.fw.core.util.FwUtils;

import java.util.Objects;

public final class SumTypeFw {
    public static final Type sumType = FW.telephonist_native("SumType", d -> {
        Val arg = d.arg();
        if (arg.equalsSymbol("cons")) {
            return FW.lambda_native(a -> FW.lambda_native(b -> d.instance(new ProductTypeFw.Product<>(a.asType(), b.asType()))));
        } else if (FwUtils.isTypeApiCall(arg, SumTypeFw.sumType)) {
            DefinitiveValEnv<Val> di = CallFw.unwrap0(arg);

            Val instance = (Val) CallFw.getVal(arg);
            arg = (Val) CallFw.getArg(arg);

            ProductTypeFw.Product<Type> pt = d.unpack(instance);
            if (arg.equalsSymbol("inject-left")) {
                return FW.lambda_native(a -> a.getType().equals(pt.a) ? di.instance(new SumInstance(a._UNPACK_(), true)) : null);
            } else if (arg.equalsSymbol("inject-right")) {
                return FW.lambda_native(b -> b.getType().equals(pt.b) ? di.instance(new SumInstance(b._UNPACK_(), false)) : null);
            } else if (FwUtils.isTypeApiCall(arg, instance.asType())) {
                instance = (Val) CallFw.getVal(arg);
                arg = (Val) CallFw.getArg(arg);

                SumInstance payload = di.unpack(instance);
                if (arg.equalsSymbol("match")) {
                    return FW.lambda(left -> FW.lambda(right -> {
                        if (payload.isLeft) {
                            return left.call(Val._NEW_INSTANCE_(pt.a, payload.payload));
                        } else {
                            return right.call(Val._NEW_INSTANCE_(pt.b, payload.payload));
                        }
                    }));
                }
//                if (arg.equalsSymbol("second")) {
//                    return payload.b;
//                }
                return null;
            }
            return null;
        }
        return null;
    }).asType();

    private static final class SumInstance {
        public final Object payload;
        public final boolean isLeft;

        public SumInstance(Object payload, boolean isLeft) {
            this.payload = payload;
            this.isLeft = isLeft;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            SumInstance that = (SumInstance) o;
            return isLeft == that.isLeft && Objects.equals(payload, that.payload);
        }

        @Override
        public int hashCode() {
            return Objects.hash(payload, isLeft);
        }

        @Override
        public String toString() {
            return isLeft ? "(" + payload + ", _)" : "(_, " + payload + ")";
        }
    }
}
