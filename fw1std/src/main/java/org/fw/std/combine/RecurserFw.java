package org.fw.std.combine;

import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.core.FW;
import org.fw.core.abstrait.LazyCallValue;
import org.fw.core.abstrait.Value;

import java.util.Objects;

public final class RecurserFw {
    public static final Val yRecurser = FW.lambda(f -> f.call(RecurserFw.yRecurser.callLazy(f)));
    public static final Val zRecurser = FW.lambda(f -> f.call(FW.lambda(new RIN(f))));

    private static class RIN implements Type.TelephonistType.LambdaCallFunction {
        private final Value f;

        public RIN(Value f) {
            this.f = f;
        }

        @Override
        public Value call(Value y) {
            return RecurserFw.zRecurser.callLazy(f).callLazy(y);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            RIN rin = (RIN) o;
            return Objects.equals(f, rin.f);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(f);
        }
    }
}
