package org.fw.std.type;

import org.fw.base.CallFw;
import org.fw.base.DefinitiveValEnv;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import org.fw.core.util.FwUtils;

import java.util.Objects;

public final class ProductTypeFw {
    public static final Type productType = FW.telephonist_native("ProductType", d -> {
        Val arg = d.arg();
        if (arg.equalsSymbol("cons")) {
            return FW.lambda_native(a -> FW.lambda_native(b -> d.instance(new Product<>(a.asType(), b.asType()))));
        } else if (arg.equalsSymbol("pair")) {
            return FW.lambda_native(a -> FW.lambda_native(b -> {
                Type type = d.instance(new Product<>(a.getType(), b.getType())).asType();
                return type.asVal().get("cons").call(a).call(b);
            }));
        } else if (FwUtils.isTypeApiCall(arg, ProductTypeFw.productType)) {
            DefinitiveValEnv<Val> di = CallFw.unwrap0(arg);

            Val instance = (Val) CallFw.getVal(arg);
            arg = (Val) CallFw.getArg(arg);

            Product<Type> pt = d.unpack(instance);
            if (arg.equalsSymbol("cons")) {
                return FW.lambda_native(
                        a -> a.getType().equals(pt.a)
                                ? FW.lambda_native(
                                b -> b.getType().equals(pt.b)
                                        ? di.instance(new Product<>(a, b))
                                        : null)
                                : null
                );
            } else if (FwUtils.isTypeApiCall(arg, instance.asType())) {
                instance = (Val) CallFw.getVal(arg);
                arg = (Val) CallFw.getArg(arg);

                Product<Value> payload = di.unpack(instance);
                if (arg.equalsSymbol("first")) {
                    return payload.a;
                }
                if (arg.equalsSymbol("second")) {
                    return payload.b;
                }
                return null;
            }
            return null;
        }
        return null;
    }).asType();

    static class Product<T> {
        public final T a, b;

        Product(T a, T b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public String toString() {
            return a + " x " + b;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Product<?> product = (Product<?>) o;
            return Objects.equals(a, product.a) && Objects.equals(b, product.b);
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b);
        }
    }
}
