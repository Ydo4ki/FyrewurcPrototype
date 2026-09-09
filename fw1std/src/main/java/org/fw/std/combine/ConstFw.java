package org.fw.std.combine;

import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;

import java.util.Objects;

public final class ConstFw {
    public static final Val _const = FW.lambda("const", c -> FW.lambda(new ConstX(c)));

    private static class ConstX implements Type.TelephonistType.LambdaCallFunction {
        private final Value c;

        private ConstX(Value c) {
            this.c = c;
        }

        @Override
        public Value call(Value ignored) {
            return c;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            ConstX constX = (ConstX) o;
            return Objects.equals(c, constX.c);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(c);
        }
    }
}
