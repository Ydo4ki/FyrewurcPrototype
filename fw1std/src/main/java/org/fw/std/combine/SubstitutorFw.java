package org.fw.std.combine;

import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.core.abstrait.Value;

import java.util.Objects;

import static org.fw.core.FW.lambda;

public final class SubstitutorFw {
    public static final Val substitutor = lambda("substitutor", x -> lambda(new Sx(x)));

    private static class Sx implements Type.TelephonistType.LambdaCallFunction {
        private final Value x;

        private Sx(Value x) {
            this.x = x;
        }

        @Override
        public Value call(Value y) {
            return lambda(new Sxy(x, y));
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Sx sx = (Sx) o;
            return Objects.equals(x, sx.x);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(x);
        }
    }

    private static class Sxy implements Type.TelephonistType.LambdaCallFunction {
        private final Value x;
        private final Value y;

        private Sxy(Value x, Value y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public Value call(Value z) {
            return x.call(z).call(y.call(z));
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Sxy sxy = (Sxy) o;
            return Objects.equals(x, sxy.x) && Objects.equals(y, sxy.y);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}
