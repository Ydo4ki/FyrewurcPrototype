package org.fw.core.base;

import org.fw.core.ast.Symbol;

public class Unspecified {
    @Deprecated
    public static final
    Val unspecified = Val.of(Val.ofTelephonist(0).asType(),
            new TelephonistType.Telephonist(() -> Symbol.of("unspecified"), (val, c) -> Unspecified.unspecified));

    public static Val unspecified(Val val, Val arg) {
        return unspecified;
    }

    public static boolean isUnspecified(Val val) {
        return val == unspecified;
    }
}
